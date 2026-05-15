# Git rules (strict version control)

## 🎯 GOAL
Ensure safe, structured, non-destructive, and professional version control.

This system is designed to:
- Protect existing working code
- Protect collaborator contributions
- Enforce structured development
- Prevent AI from breaking the system

---

# 🔒 CRITICAL NON-NEGOTIABLE RULES

## 1. CODE PROTECTION (ABSOLUTE)
The AI MUST NEVER:

- Delete any line of code that:
  - Was committed and accepted
  - Is working correctly
  - Was written by another collaborator

- Modify existing working logic unless explicitly instructed

- Override collaborator code

---

## 2. HISTORY PROTECTION

The AI MUST NEVER:

- Force push
- Rebase shared branches
- Rewrite commit history
- Squash commits that already exist

---

## 🚫 ABSOLUTE PROHIBITIONS

- NO deleting working features
- NO removing existing logic
- NO replacing code without adding a new version
- NO editing other people's code
- NO refactor by deletion

---

# 📦 COMMIT STRUCTURE (MANDATORY)

Every commit MUST follow this format:

[ID] TYPE: Description

---

## 🔢 COMMIT NUMBERING SYSTEM

- MUST start from: 001
- MUST be sequential
- MUST NEVER skip or reuse numbers

---

## 🧾 COMMIT TYPES

- INIT
- FEATURE
- UI
- FIX
- TEST
- DOC

---

## ✅ EXAMPLES

[001] INIT: Project structure setup  
[002] FEATURE: Event Factory implementation  
[003] FEATURE: Menu Builder implementation  
[004] FEATURE: Add-ons Decorator  
[005] FEATURE: Order Facade  
[006] FEATURE: Payment Adapter  
[007] FEATURE: Order State System  
[008] FEATURE: Notification Observer  
[009] UI: Login Page  
[010] UI: Dashboard  

---

# 🧩 FEATURE-BASED COMMIT RULE

Each page, pattern, or feature MUST be its own commit.

---

# 🌿 BRANCHING STRATEGY

- Use feature branches only
- Naming:
  feature/menu-builder  
  feature/payment-adapter  
  ui/dashboard  

- NEVER commit directly to main

---

# 🚀 PUSH POLICY

- DO NOT push to GitHub unless explicitly requested

---

# 🔁 CHANGE POLICY

- DO NOT edit old code
- DO NOT delete existing code
- ALWAYS create a new commit for changes

---

# 🧪 PRE-COMMIT VALIDATION

- Code compiles
- No runtime errors
- Feature works
- Nothing is broken

---

# 🔐 COLLABORATION RULE

- Do not touch collaborator code
- Extend only

---

# 🧠 AI RULES

- Commit after each feature
- Keep commits small
- Follow numbering

---

# ⚠️ FAILURE CONDITIONS

- Code deletion
- Broken features
- Wrong commit format
- Modified history

---

# ✅ FINAL RULE

Code is valid ONLY if:
- No code removed
- No collaborator code modified
- System stable
