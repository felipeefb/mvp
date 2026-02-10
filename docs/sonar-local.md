# Running Sonar locally

This repo is wired to SonarCloud (`felipeefb_mvp`, org `felipeefb`). You can run the analysis locally on any branch before pushing.

## Prereqs
- Java 21
- `sonar-scanner` installed and on `PATH` (`sonar-scanner --version`)
- Sonar token with “Execute analysis” permission on the org/project. Export it as `SONAR_TOKEN`.
- Host URL: `https://sonarcloud.io`

## Commands
Analyze current branch (standalone scanner):
```bash
export SONAR_TOKEN=your_token
export SONAR_HOST_URL=https://sonarcloud.io

sonar-scanner \
  -Dsonar.projectKey=felipeefb_mvp \
  -Dsonar.organization=felipeefb \
  -Dsonar.host.url=$SONAR_HOST_URL \
  -Dsonar.login=$SONAR_TOKEN \
  -Dsonar.branch.name=$(git rev-parse --abbrev-ref HEAD) \
  -Dsonar.sources=src/main/java \
  -Dsonar.tests=src/test/java \
  -Dsonar.java.binaries=**/build/classes/java/main \
  -Dsonar.junit.reportPaths=**/build/test-results/test \
  -Dsonar.coverage.jacoco.xmlReportPaths=**/build/reports/jacoco/test/jacocoTestReport.xml \
  -Dsonar.qualitygate.wait=true
```

Tip: the `.githooks/pre-push` hook sources `.env` automatically and runs the same analysis when `SONAR_*` vars are present.

## Coverage
`./gradlew test jacocoTestReport jacocoTestCoverageVerification` enforces 90% minimum line coverage. Sonar picks up XML reports automatically (`**/build/reports/jacoco/test/jacocoTestReport.xml`).

## IntelliJ IDEA setup
1) Install **SonarLint** plugin.
2) Connect to SonarCloud:
   - Settings → Tools → SonarLint → Connections → Add.
   - Type: SonarCloud. Log in with token `SONAR_TOKEN`, org key `felipeefb`.
   - Bind the project to `felipeefb_mvp`.
3) Enable “Bind project automatically” so rules/quality profile match the cloud (“Sonar way”).
4) Enable “Show annotations” to see issues inline; optionally enable “On-the-fly” analysis for immediate feedback.

MCP reminder for IDE:
- IntelliJ does not use MCP; continue to use Codex CLI with MCP pointing to `http://127.0.0.1:3000/mcp` and `NOTION_MCP_AUTH_TOKEN`.

## Codex CLI quick check
```bash
SONAR_HOST_URL=https://sonarcloud.io SONAR_TOKEN=$SONAR_TOKEN sonar-scanner -Dsonar.projectKey=felipeefb_mvp -Dsonar.organization=felipeefb -Dsonar.sources=src/main/java -Dsonar.tests=src/test/java -Dsonar.java.binaries=**/build/classes/java/main -Dsonar.junit.reportPaths=**/build/test-results/test -Dsonar.coverage.jacoco.xmlReportPaths=**/build/reports/jacoco/test/jacocoTestReport.xml -Dsonar.qualitygate.wait=true
```
If secrets are missing or you’re on a non-`development`/`main` branch, CI will skip cloud analysis but still enforce coverage.

## MCP / Notion reminder
MCP server stays the same (http://127.0.0.1:3000/mcp with `NOTION_MCP_AUTH_TOKEN`). IntelliJ doesn’t consume MCP, but Codex CLI does; no extra IDE config needed for MCP.
