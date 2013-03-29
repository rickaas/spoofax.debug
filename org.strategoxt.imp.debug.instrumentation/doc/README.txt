DSL Debugger Instrumentation

create a meta-dsl that allows the user to specify using SDF sorts
which syntax constructs can fire debug events

For example:


exports

  context-free start-symbols
    Start

  context-free syntax
  
    "module" ID Definition*       -> Start {cons("Module")}
    "entity" ID "{" Property* "}" -> Definition {cons("Entity")}
    ID ":" Type                   -> Property {cons("Property")}
    ID                            -> Type {cons("Type")}