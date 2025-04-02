# ⚙️ C-Minus Compiler (Java)

A full compiler for a simplified C-like language called **C-Minus**, written in Java. The compiler supports three main stages:

1. **Parsing**: Converts `.cm` source files into abstract syntax trees (ASTs)
2. **Semantic Analysis**: Builds symbol tables and performs type checking
3. **Code Generation**: Outputs assembly code in Tiny Machine (TM) language

---

## 🧑‍💻 Tech Stack

<table>
  <tr>
    <th>Technology</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg" width="30"/> Java </td>
    <td>The primary language used to build the compiler.</td>
  </tr>
  <tr>
    <td><img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/bash/bash-original.svg" width="30"/> Lex </td>
    <td>Used for lexical analysis to tokenize the C-Minus source code.</td>
  </tr>
</table>


---

## 📂 Git Clone Instructions

To get started with the project, clone the repository to your local machine:

```bash
git clone https://github.com/your-username/c-minus-compiler.git
cd c-minus-compiler
```

---

## 👩‍💻 Authors

- Isabella McIvor  
- Huda Nadeem  
- Zuya Abro

---

## 🧠 Project Structure

The compiler is structured in stages. You can run each stage individually or run the full compiler:

| Stage | Flag | Output |
|-------|------|--------|
| Parser only | `-a` | `<filename>.abs` (syntax tree) |
| Parser + Semantic Analyzer | `-s` | `<filename>.abs`, `<filename>.sym` (symbol table) |
| Full Compiler | `-c` | `<filename>.abs`, `<filename>.sym`, `<filename>.tm` (assembly code) |

---

## 🛠️ How to Compile

Make sure you're in the root directory:

```bash
make
```

---

## ▶️ How to Run

### Run Parser Only

```bash
java -cp /usr/share/java/cup.jar:. CM -a tests/<filename>.cm
```

### Run Parser + Semantic Analyzer

```bash
java -cp /usr/share/java/cup.jar:. CM -s tests/<filename>.cm
```

### Run Full Compiler (Parser + Semantic + CodeGen)

```bash
java -cp /usr/share/java/cup.jar:. CM -c tests/<filename>.cm
```
