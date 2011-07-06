var matches = Search.forReferencesToMethod("com.example.MyNumber(Double)");

var edit = new MultiSourceChange();
foreach(filter(matches, Search.onlySourceMatches),
    function(match) {
        edit.changeFile(match.getElement().getCompilationUnit())
            .addImport(match.getElement().getCompilationUnit(), "static com.example.MyNumber.valueOf")
            .addEdit(Refactor.createReplaceMethodCallEdit(match.getElement().getCompilationUnit(), match.getOffset(), match.getLength(), "valueOf"));
    });
edit.apply();
