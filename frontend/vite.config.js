import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    host: true,
    proxy: {
      "/api": "http://localhost:8080",
      "/v3/api-docs": "http://localhost:8080",
    },
  },
  build: {
    sourcemap: true,
  },
  test: {
    environment: "jsdom",
    globals: true,
    setupFiles: "./src/setupTests.js",
    coverage: {
      provider: "v8",
      include: ["src/**/*.{js,jsx}"],
      exclude: [
        "src/index.jsx",
        "src/error-page.jsx",
        "src/App.jsx",
        "src/components/formGenerator/inputs/tagSelector.jsx",
      ],
    },
  },
});
