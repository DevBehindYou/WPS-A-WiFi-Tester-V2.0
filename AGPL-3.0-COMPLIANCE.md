# AGPL-3.0 Compliance Instructions

## Purpose

This repository is a modified and substantially improved version of an existing project that was originally distributed under the GNU Affero General Public License, version 3 (AGPL-3.0).

The goal of this document is to ensure that all modifications and distribution of this project remain compliant with the AGPL-3.0 license.

The AI agent MUST follow these instructions when modifying, restructuring, building, or preparing this repository for publication.

---

# 1. First: Inspect the Original Repository

Before making changes, inspect the repository and identify:

- The original copyright holders.
- The original author's name.
- The original repository/project name.
- The original repository URL, if present.
- Existing `LICENSE`, `COPYING`, or similar files.
- Existing copyright headers.
- Existing attribution notices.
- Existing third-party licenses.
- Existing README licensing information.
- Existing package/dependency manifests.
- Any files that contain explicit licensing notices.

DO NOT remove or overwrite original copyright notices merely because the code has been heavily modified.

---

# 2. Preserve the AGPL-3.0 License

The repository MUST continue to be distributed under AGPL-3.0 where the AGPL applies.

Do NOT:

- Replace AGPL-3.0 with MIT.
- Replace AGPL-3.0 with Apache-2.0.
- Remove the original AGPL license.
- Add a proprietary license that conflicts with AGPL.
- Claim that the entire project is newly created if it contains substantial original code.
- Remove original attribution.

The existing AGPL-3.0 license text should remain available in the repository.

If a `LICENSE` file already exists and contains the official AGPL-3.0 text, preserve it unless there is a compelling reason to update it.

---

# 3. Clearly Identify Our Modifications

Because this repository contains modifications to an existing AGPL-3.0 project, document that fact clearly.

Add or update the README with a section similar to:

> ## Original Project and Modifications
>
> This project is based on an existing project originally distributed under the GNU Affero General Public License, version 3 (AGPL-3.0).
>
> This version contains substantial modifications and improvements, including changes to the application architecture, navigation, UI, themes, integrations, backend functionality, and other components.
>
> Original copyright and license notices have been retained where applicable.
>
> See the repository history and source files for additional information about the modifications.

Do NOT invent specific changes.

The AI agent should describe only modifications that actually exist in the repository.

---

# 4. Add a Modification Notice

Where appropriate, add a file such as:

`NOTICE.md`

or a section in `README.md`.

It should identify:

1. The original project.
2. The original author/copyright holder, if known.
3. The original repository URL, if known.
4. The fact that this repository is a modified version.
5. The major categories of modifications.

Example:

```text
# Modification Notice

This project is based on:

Original Project: [PROJECT NAME]
Original Repository: [REPOSITORY URL]
Original Copyright: [COPYRIGHT HOLDER]

The current repository contains modifications to the original project,
including architectural changes, UI changes, navigation, theme support,
integration improvements, backend functionality, bug fixes, and other
enhancements.

The original project was distributed under the GNU Affero General Public
License, version 3 (AGPL-3.0).
```

Replace placeholders with verified information.

NEVER fabricate attribution information.

---

# 5. Copyright

Do NOT automatically replace the original copyright holder with our name.

If a file contains something such as:

```text
Copyright (c) ORIGINAL AUTHOR
```

preserve it unless there is a legitimate reason to modify the notice.

If we add substantial original code, an appropriate additional copyright notice may be added.

For example:

```text
Copyright (c) ORIGINAL AUTHOR
Copyright (c) CURRENT CONTRIBUTORS
```

Only use accurate names/organizations and appropriate years.

Do not falsely claim ownership of someone else's original code.

---

# 6. Source Code Availability

If this project is distributed to other users, the corresponding source code obligations of AGPL-3.0 MUST be respected.

The published project should contain the source code necessary to build and modify the covered software.

Do NOT publish only:

* An installer
* An executable
* A compiled DLL
* A packaged application

while intentionally withholding the corresponding source code when the AGPL requires it.

The repository should remain capable of providing the corresponding source code for the distributed version.

---

# 7. Network / Server Functionality

AGPL-3.0 has an additional requirement for software that users interact with over a network.

If this project contains server-side functionality or allows users to interact with modified AGPL-covered software through a network, investigate the AGPL requirements regarding the corresponding source code.

If applicable, the application MUST provide an appropriate way for users to obtain the corresponding source code for the version they are interacting with.

The agent should NOT assume that a desktop application is automatically exempt.

Inspect whether:

* The application contains a server.
* The application communicates with a backend.
* The backend contains AGPL-covered code.
* Users interact with the software remotely.
* Modified AGPL components are used on a server.
* The project exposes functionality over HTTP, APIs, WebSockets, RPC, etc.

If any of these apply, flag the situation for review.

---

# 8. Do Not Remove Attribution

Do NOT remove:

* Copyright headers
* License headers
* Author attribution
* Original repository references
* AGPL notices
* Third-party license notices

unless the removal is clearly permitted and appropriate.

When refactoring files, preserve relevant notices.

---

# 9. Third-Party Dependencies

Inspect every dependency used by the project.

For each dependency, determine:

* Name
* Version
* License
* Source/repository
* Whether attribution is required
* Whether its license is compatible with the project's distribution model

Pay special attention to:

* GPL
* AGPL
* LGPL
* MIT
* Apache-2.0
* BSD
* MPL
* Proprietary licenses
* Commercial dependencies

Do NOT assume that every dependency can automatically be redistributed.

---

# 10. Create a Third-Party Licenses File

If the project contains many third-party dependencies, create:

`THIRD-PARTY-LICENSES.md`

Document relevant dependencies in a format such as:

```markdown
# Third-Party Licenses

## Dependency Name

Version: X.Y.Z
License: MIT
Repository: [verified URL]

Copyright:
[verified copyright notice]

License text:
[appropriate license information]
```

Do not copy license text blindly.

Use the actual license information associated with the dependency/version being distributed.

---

# 11. Do Not Modify Dependency Licenses

Never modify a dependency's license in order to make it appear compatible.

For example, do NOT change:

```text
MIT
```

into:

```text
AGPL-3.0
```

or vice versa.

Each third-party component retains its applicable license.

---

# 12. README Requirements

The README should clearly communicate:

* What the project is.
* That it is based on an existing project, if applicable.
* The original project attribution.
* The fact that modifications were made.
* The project's applicable license.
* How to obtain the source code.
* How to build the project.
* Major modifications/improvements.
* Third-party dependencies where appropriate.

Do not make misleading claims such as:

> "Built entirely from scratch"

if the project contains substantial code from the original repository.

---

# 13. License Header for New Files

For NEW source files created as part of this project, use an appropriate AGPL-3.0 license header when consistent with the project's existing conventions.

Example:

```text
Copyright (c) [YEAR] [AUTHOR/ORGANIZATION]

This file is part of [PROJECT NAME].

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
```

Use the project's existing header style if one already exists.

Do not add incorrect copyright information.

---

# 14. Do Not Rewrite History

Do NOT rewrite Git history merely to hide the fact that the project was based on another repository.

Do NOT:

* Delete commits solely to conceal the origin.
* Remove attribution to make the project appear entirely original.
* Strip copyright information.
* Rewrite authorship without a legitimate reason.

Normal development practices such as squashing commits are a separate matter, but they must not be used to misrepresent authorship or licensing.

---

# 15. GitHub Repository Information

Before publishing, inspect:

* Repository description
* README
* LICENSE
* Copyright headers
* About section
* Package metadata
* Release notes

Make sure the repository does not falsely state that all code was independently created if it contains modified AGPL-covered code.

---

# 16. Release Packages

If distributing releases such as:

```text
.exe
.msi
.zip
.tar.gz
```

make sure the release process complies with the applicable AGPL source-code requirements.

The release should identify:

* Project version
* Source repository
* Applicable license
* Relevant attribution
* How users can obtain corresponding source code

Do not distribute a modified binary while intentionally making the corresponding source unavailable when the license requires it.

---

# 17. Do Not Add Restrictions

Do NOT add terms that contradict the AGPL.

For example, do not add statements such as:

```text
You may not modify this software.
```

or:

```text
You may not redistribute this software.
```

or:

```text
Commercial use is prohibited.
```

unless such restrictions are being applied to a separate component under a license that legally permits them.

AGPL-3.0 grants rights that cannot simply be removed by adding arbitrary restrictions.

---

# 18. Commercial Use

AGPL-3.0 does NOT automatically prohibit commercial use.

Do not add:

```text
NON-COMMERCIAL USE ONLY
```

to AGPL-covered code.

A business can commercially distribute AGPL software, subject to the license's requirements.

If this project will be sold commercially, ensure the distribution model is reviewed for AGPL compliance.

---

# 19. Proprietary Components

If we introduce proprietary components, services, APIs, SDKs, or other closed-source components, inspect whether their licenses permit the intended combination and distribution.

Do NOT assume that adding proprietary code to an AGPL project automatically makes the entire project proprietary.

If there is uncertainty about whether a component can legally be combined with the AGPL-covered code, FLAG IT for human/legal review rather than guessing.

---

# 20. Backend Separation

If the project has a backend, determine whether it is:

* Part of the same AGPL-covered work.
* A separate independent service.
* A separate program communicating through an API.
* A third-party service.

Document the architecture.

Do not make legal conclusions solely based on whether components communicate through HTTP.

If the boundary between components is legally significant, FLAG IT for review.

---

# 21. Automated Compliance Audit

Before considering the repository complete, perform the following checks.

### License

* [ ] AGPL-3.0 license is present.
* [ ] Original license text has not been removed.
* [ ] No conflicting project-level license was introduced.
* [ ] License references are correct.

### Attribution

* [ ] Original author attribution is preserved.
* [ ] Original copyright notices are preserved.
* [ ] Original repository is identified where appropriate.
* [ ] Modifications are documented.
* [ ] No false authorship claims exist.

### Source Code

* [ ] Corresponding source code is available where required.
* [ ] Build instructions exist.
* [ ] Release process does not intentionally hide required source code.

### Network Functionality

* [ ] Server/backend components were inspected.
* [ ] Network-accessible AGPL functionality was identified.
* [ ] AGPL's network source-code requirements were considered.

### Dependencies

* [ ] Dependencies were inventoried.
* [ ] Licenses were checked.
* [ ] Third-party notices are preserved.
* [ ] Incompatible/proprietary dependencies were flagged.

### Documentation

* [ ] README contains licensing information.
* [ ] README describes the relationship to the original project.
* [ ] Modification notice exists.
* [ ] Third-party licensing information exists where necessary.

---

# 22. Agent Behavior

When performing this compliance work, the AI agent MUST:

1. Inspect the actual repository before making claims.
2. Never invent copyright holders.
3. Never invent license information.
4. Never remove original attribution.
5. Never replace AGPL-3.0 with another license without explicit authorization.
6. Never claim that code is original when it is derived from the original repository.
7. Preserve third-party license notices.
8. Flag uncertain legal questions instead of guessing.
9. Make only technically necessary documentation changes.
10. Keep the project's functionality unchanged unless explicitly instructed otherwise.

---

# 23. Important: Legal Review

This document is an engineering/compliance checklist, NOT legal advice.

AGPL compliance can depend on the exact architecture, how code was copied or modified, how components are combined, and how the software is distributed.

If there is uncertainty about whether a particular component constitutes a derivative work, whether a backend is covered, or what source must be provided, FLAG the issue for review by someone qualified to provide software-license legal advice.

---

# 24. Final Deliverables

After completing the audit, the agent should produce:

1. Updated `README.md`
2. Preserved/verified `LICENSE`
3. `NOTICE.md` if appropriate
4. `THIRD-PARTY-LICENSES.md` if appropriate
5. Appropriate license headers for newly created source files
6. Updated build/source-distribution documentation
7. A final compliance report

The final compliance report should contain:

```markdown
# AGPL Compliance Report

## Original Project

- Name:
- Repository:
- Original Author:
- License:

## Changes Made

- ...
- ...
- ...

## License Files

- ...

## Attribution

- ...

## Third-Party Dependencies

- ...

## Network Functionality

- ...

## Source Code Availability

- ...

## Potential Issues

- ...

## Items Requiring Human/Legal Review

- ...

## Conclusion

Describe whether the repository appears technically prepared
for AGPL-3.0-compliant distribution, while clearly identifying
anything that still requires review.
```

---

# FINAL RULE

**Do not treat this checklist as permission to remove attribution or change licensing.**

The objective is to make the project a properly documented and transparently modified AGPL-3.0 project—not to hide its origin or circumvent the original license.
