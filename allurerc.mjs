// Allure 3 configuration (detected automatically by allure-maven).
// From the same test results it generates:
//   - awesome: report grouped by @Epic > @Feature > @Story.
//   - layers: report grouped by epic and feature, then by layer (@ControllerTest,
//     @ServiceTest...) and isolation (@IsolatedTest / @SociableTest).
//   - traceability: Markdown traceability matrix between user stories and tests,
//     written to docs/deliverables/D3 (see src/test/allure/traceability-plugin.mjs).
// See the annotations in src/test/java/org/springframework/samples/petclinic/testing.
import { mkdirSync, writeFileSync } from "node:fs";
import { join } from "node:path";
import { fileURLToPath, pathToFileURL } from "node:url";

// Prefix of the user story ids. It must match both the headings in the
// requirements document (### HU-01: ...) and the tests (@Story("HU-01: ...")).
const STORY_PREFIX = "HU";

const fromProject = (path) => fileURLToPath(new URL(path, import.meta.url));

// Allure 3.4 only loads plugins installed as "@allurereport/plugin-<name>" packages
// (local file paths fail). This registers a local plugin as such a package in
// .allure/node_modules, next to the Allure install, and returns its name.
const localPlugin = (name, file) => {
  const dir = fromProject(`./.allure/node_modules/@allurereport/plugin-${name}`);
  mkdirSync(dir, { recursive: true });
  writeFileSync(join(dir, "package.json"),
    JSON.stringify({ name: `@allurereport/plugin-${name}`, type: "module", main: "index.js" }));
  writeFileSync(join(dir, "index.js"),
    `export { default } from ${JSON.stringify(pathToFileURL(fromProject(file)).href)};\n`);
  return name;
};

export default {
  plugins: {
    awesome: {
      options: {
        reportName: "Tests by epic / feature",
        groupBy: ["epic", "feature", "story"],
      },
    },
    layers: {
      import: "@allurereport/plugin-awesome",
      options: {
        reportName: "Tests by layer / isolation",
        singleFile: true,
        groupBy: ["epic", "feature", "layer", "isolation"],
      },
    },
    traceability: {
      import: localPlugin("dp1-traceability", "./src/test/allure/traceability-plugin.mjs"),
      options: {
        output: fromProject("./docs/deliverables/D3/Matriz de trazabilidad.md"),
        requirements: fromProject("./docs/deliverables/Análisis de requisitos del sistema.md"),
        sourceRoot: fromProject("./src/test/java"),
        storyPrefix: STORY_PREFIX,
      },
    },
  },
};
