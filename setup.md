# Setup

How to clone, build, and contribute to clean-chess in Eclipse.

The project ships its own Checkstyle config and Eclipse JDT compiler / formatter / cleanup / save-actions settings. Once you have Eclipse, the JDK, and the Checkstyle plug-in in place, importing the project pulls those settings in automatically. No manual file copies, no `.epf` import.

Personal preferences beyond the requirements below — extra plugins, AI tooling, Markdown viewers, alternate IDEs — are not part of this document.

---

## 1. Install Eclipse

1. Download Eclipse installer from <https://www.eclipse.org/downloads/>.
2. Run the installer.
3. Select **Eclipse IDE for Java Developers**.
4. Install using all defaults.

> *Note (per 2026-05-02):* the installer ships with a Java 21+ VM. When asked to exclude Eclipse from Microsoft Defender scanning, accepting is recommended.

## 2. Eclipse personal preferences

These are workspace-level (not project-portable) cosmetic preferences. Optional but recommended.

Under **Window → Preferences**:

- **General → Editors → Text Editors** — uncheck *Show line numbers*.
- **General → Editors → Text Editors → Spelling** — uncheck *Enable spell checking*.
- **General → Editors → File Associations**:
  - Add extension `*.bat`, make *Plain Text Editor* the default.
  - Add extension `*.cmd`, make *Plain Text Editor* the default.
  - Add extension `*.pgn`, add *Plain Text Editor* as associated editor.
- **General → Workspace → Build** — check *Save automatically before manual build*.

Click **Apply and Close**.

## 3. Install Java

clean-chess targets **JDK 17** (`maven.compiler.release` in `pom.xml`).

### Install

1. Open the Temurin website: <https://adoptium.net/temurin/releases?version=17&os=any&arch=any>.
2. Download the latest MSI for JDK 17.
3. Install using all defaults.

### Configure in Eclipse

Add a new JRE under **Window → Preferences → Java → Installed JREs** pointing at the install folder:

```
C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot
```

Adapt the folder for newer point releases.

## 4. Install the Checkstyle plug-in

Install the Checkstyle plug-in from **Eclipse Marketplace** using all defaults.

> The Checkstyle rules are checked into this repo (`checkstyle.xml`, `checkstyle-suppressions.xml`, `checkstyle-xpath-suppressions.xml`). Once the project is imported, the plug-in picks them up automatically — no manual XML copy required.

## 5. Import clean-chess

1. **File → Import… → Git → Projects from Git (with smart import)**.
2. Click **Next**, then **Clone URI**.
3. Enter:
   - URI: `https://github.com/dlbbld/clean-chess.git`
   - User name: your GitHub user name
   - Password: your GitHub personal access token (PAT)

   *You can save the password in the secure store.*
4. Use all defaults to complete the import.

### Switch to the active development branch

The project develops on a single branch.

1. Right-click the project → **Team → Switch To → Other…**
2. Expand **Remote tracking**.
3. Click the active development branch.
4. Click **Check Out…** → **Check out as New Local Branch** → **Finish**.

## 6. Optional: install Python for oracle regeneration

The normal Maven test suite does not require Python. Python is only needed when PGN fixtures change and the committed python-chess oracle JSONL files must be regenerated.

1. Install Python 3.
2. From the repo root, install the pinned python-chess dependency:

```
pip install -r src/test/python/requirements.txt
```

The regeneration commands live in the generator module docstrings:

```
python src/test/python/generate_pgn_import_oracle.py
python src/test/python/generate_move_gen_oracle.py
```

---

## What you get for free after import

Because of the project-level config in `.settings/` and at the repo root, you do **not** need to:

- Copy any Checkstyle XML to a workspace path — the plug-in reads `checkstyle.xml` directly from the project.
- Import an `.epf` preferences file — `.settings/org.eclipse.jdt.core.prefs` and `.settings/org.eclipse.jdt.ui.prefs` carry the JDT compiler warnings, formatter rules, cleanup profile, and save-actions configuration.
- Configure Save Actions or Cleanup profiles — they're already applied per-project.

The only manual step that affects the code is installing the Checkstyle plug-in itself (the rules can't activate without it).
