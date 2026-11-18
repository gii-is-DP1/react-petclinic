// Allure 3 plugin that writes a traceability matrix (Markdown) from the test results.
//
// It relates user stories, epics and features with the tests that cover them, using
// the labels added by the test annotations:
//   @Epic, @Feature, @Story("HU-01: Name")  -> epic / feature / user story
//   @ControllerTest, @ServiceTest, ...       -> layer
//   @IsolatedTest, @SociableTest             -> isolation
//   @Description                             -> description
// If a requirements file is given, its "### HU-xx: Name" headings are read too, so
// user stories without tests also show up in the matrix.
//
// Options (all paths absolute):
//   output        Markdown file to write (default: traceability.md inside the report).
//   requirements  Markdown file with the user stories (optional).
//   sourceRoot    Root of the test sources, used to link each test to its file (optional).
//   storyPrefix   Prefix of the user story ids, "HU" by default (HU-01, HU-02...).

import { mkdir, readFile, writeFile } from "node:fs/promises";
import { dirname, isAbsolute, join, relative } from "node:path";

const LAYERS = {
  controller: "Controlador",
  service: "Servicio",
  repository: "Repositorio",
  model: "Modelo",
  security: "Seguridad",
};
const ISOLATION = { isolated: "aislada", sociable: "sociable" };
const STATUS = {
  passed: "✅ Pasa",
  failed: "❌ Falla",
  broken: "⚠️ Error",
  skipped: "⏭️ Omitida",
  unknown: "❔ Desconocido",
};
const NO_STORY = "(sin historia de usuario)";

const labelValues = (tr, name) => [
  ...new Set(tr.labels.filter((l) => l.name === name && l.value).map((l) => l.value)),
];

const cell = (value) =>
  String(value ?? "")
    .replace(/\|/g, "\\|")
    .replace(/\s*\n\s*/g, " ")
    .trim();

const escapeRegExp = (text) => text.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");

// Builds the functions that recognise user story ids with the given prefix,
// e.g. "HU" for "HU-01: Iniciar sesión".
const storyIds = (prefix) => {
  const id = `${escapeRegExp(prefix)}-?\\s*(\\d+)`;
  return {
    // "HU-01: Iniciar sesión" and "HU-1" refer to the same story.
    key: (story) => {
      const match = story.match(new RegExp(`^\\s*${id}`, "i"));
      return match ? `${prefix}-${Number(match[1])}` : story;
    },
    // Markdown headings such as "### HU-01: Name ([link](url))".
    heading: new RegExp(`^[ \\t]*#{2,6}[ \\t]*(${id})[ \\t]*[:.-]?[ \\t]*(.*)$`, "gim"),
  };
};

const capitalize = (text) => text.charAt(0).toUpperCase() + text.slice(1);

const testType = (tr) => {
  const layer = labelValues(tr, "layer").map((l) => LAYERS[l] ?? capitalize(l)).join("/");
  const isolation = labelValues(tr, "isolation").map((i) => ISOLATION[i] ?? i).join("/");
  if (!layer && !isolation) {
    return "Sin clasificar";
  }
  return isolation ? `${layer || "?"} (${isolation})` : layer;
};

// Natural order, so that HU-2 goes before HU-10.
const byText = (a, b) => a.localeCompare(b, undefined, { numeric: true });

// Compares lists of values; undefined values go last.
const compareKeys = (a, b) => {
  for (let i = 0; i < a.length; i++) {
    if (a[i] === b[i]) continue;
    if (a[i] === undefined) return 1;
    if (b[i] === undefined) return -1;
    return byText(a[i], b[i]);
  }
  return 0;
};

const isFailure = (tr) => tr.status === "failed" || tr.status === "broken";

// Reads the user story headings, ignoring template placeholders without a number.
const readStories = async (file, ids) => {
  const text = await readFile(file, "utf-8");
  const stories = [];
  for (const match of text.matchAll(ids.heading)) {
    const name = match[3]
      .replace(/\s*\(\s*\[[^\]]*\]\([^)]*\)\s*\)?/g, "")
      .replace(/\[([^\]]*)\]\([^)]*\)/g, "$1")
      .trim();
    stories.push({ key: ids.key(match[1]), title: name ? `${match[1].trim()}: ${name}` : match[1].trim() });
  }
  return stories;
};

export default class TraceabilityPlugin {
  constructor(options = {}) {
    this.options = options;
  }

  async done(context, store) {
    const { output, requirements, sourceRoot, storyPrefix = "HU" } = this.options;
    const ids = storyIds(storyPrefix);
    const storyKey = ids.key;
    const results = await store.allTestResults();
    const defined = requirements ? await readStories(requirements, ids).catch(() => []) : [];
    const outputDir = output && isAbsolute(output) ? dirname(output) : undefined;

    const sourceLink = (tr) => {
      const testClass = labelValues(tr, "testClass")[0];
      if (!testClass || !sourceRoot || !outputDir) {
        return undefined;
      }
      const file = join(sourceRoot, ...testClass.replace(/\$.*/, "").split(".")) + ".java";
      return encodeURI(relative(outputDir, file).split("\\").join("/"));
    };

    const testName = (tr) => {
      const testClass = labelValues(tr, "testClass")[0];
      const name = testClass ? `${testClass.split(".").pop()} › ${tr.name}` : tr.name;
      const link = sourceLink(tr);
      return link ? `[${cell(name)}](${link})` : cell(name);
    };

    // Stories defined in the requirements plus the ones only found in the tests.
    const stories = new Map(defined.map((s) => [s.key, { ...s, defined: true, tests: [] }]));
    const rows = [];
    for (const tr of results) {
      const testStories = labelValues(tr, "story").sort(byText);
      for (const story of testStories.length ? testStories : [NO_STORY]) {
        const key = story === NO_STORY ? NO_STORY : storyKey(story);
        if (!stories.has(key)) {
          stories.set(key, { key, title: story, defined: false, tests: [] });
        }
        stories.get(key).tests.push(tr);
      }
      rows.push({
        epic: labelValues(tr, "epic").join(", ") || "—",
        feature: labelValues(tr, "feature").join(", ") || "—",
        story: testStories.map((s) => stories.get(storyKey(s)).title).join("<br>") || NO_STORY,
        test: testName(tr),
        description: tr.description || "",
        type: testType(tr),
        status: STATUS[tr.status] ?? tr.status,
        // Tests without epic, feature or story go last in their group.
        sortKey: [labelValues(tr, "epic")[0], labelValues(tr, "feature")[0], testStories[0],
          labelValues(tr, "testClass")[0] ?? "", tr.name],
      });
    }
    rows.sort((a, b) => compareKeys(a.sortKey, b.sortKey));

    const ordered = [
      ...[...stories.values()].filter((s) => s.key !== NO_STORY).sort((a, b) => byText(a.title, b.title)),
      ...(stories.has(NO_STORY) ? [stories.get(NO_STORY)] : []),
    ];
    const types = [...new Set(results.map(testType))].sort();

    const storyStatus = (s) => {
      if (s.key === NO_STORY) return "—";
      if (s.tests.length === 0) return "⚠️ Sin pruebas";
      if (s.tests.some(isFailure)) return "❌ Con fallos";
      return "✅ Cubierta";
    };

    const passed = results.filter((tr) => tr.status === "passed").length;
    const failed = results.filter(isFailure).length;
    const untested = ordered.filter((s) => s.key !== NO_STORY && s.tests.length === 0);
    const undefinedStories = defined.length
      ? ordered.filter((s) => !s.defined && s.key !== NO_STORY)
      : [];

    const lines = [
      "# Matriz de trazabilidad",
      "",
      "> Generada automáticamente al ejecutar `./mvnw verify` a partir de las anotaciones de los tests",
      "> (`src/test/allure/traceability-plugin.mjs`). No la edites a mano: cambia las anotaciones",
      `> (\`@Epic\`, \`@Feature\`, \`@Story("${storyPrefix}-xx: Nombre")\`, \`@Description\` y las de capa/tipo del`,
      "> paquete `testing`) y vuelve a generar el informe.",
      "",
      `**${results.length}** pruebas: ${passed} pasan, ${failed} fallan.`,
      "",
      "## 1. Resumen por historia de usuario",
      "",
      `| ${["Historia de usuario", "Estado", "Pruebas", ...types.map(cell)].join(" | ")} |`,
      `|${["Historia de usuario", "Estado", "Pruebas", ...types].map(() => "---|").join("")}`,
      ...ordered.map((s) => {
        const counts = types.map((t) => s.tests.filter((tr) => testType(tr) === t).length || "");
        return `| ${[cell(s.title), storyStatus(s), s.tests.length, ...counts].join(" | ")} |`;
      }),
      "",
    ];

    if (untested.length || undefinedStories.length) {
      lines.push("## 2. Avisos", "");
      for (const s of untested) {
        lines.push(`- ⚠️ **${cell(s.title)}** no tiene ninguna prueba.`);
      }
      for (const s of undefinedStories) {
        lines.push(`- ❔ **${cell(s.title)}** se usa en las pruebas pero no aparece en el documento de requisitos.`);
      }
      lines.push("");
    }

    lines.push(
      `## ${untested.length || undefinedStories.length ? 3 : 2}. Detalle de las pruebas`,
      "",
      "| Épica | Feature | Historia de usuario | Prueba | Descripción | Tipo | Estado |",
      "|---|---|---|---|---|---|---|",
      ...rows.map((r) =>
        `| ${cell(r.epic)} | ${cell(r.feature)} | ${cell(r.story)} | ${r.test} | ${cell(r.description)} | ${r.type} | ${r.status} |`),
      "",
    );

    const content = Buffer.from(lines.join("\n"), "utf-8");
    if (output && isAbsolute(output)) {
      await mkdir(dirname(output), { recursive: true });
      await writeFile(output, content);
    } else {
      await context.reportFiles.addFile(output || "traceability.md", content);
    }
  }
}
