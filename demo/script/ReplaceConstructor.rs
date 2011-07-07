var matches = Search.forReferencesToMethod("com.example.MyNumber(String)");

var edit = new MultiSourceChange();
foreach(filter(matches, Search.onlySourceMatches),
    function(match) {
        edit.changeFile(match.getElement().getCompilationUnit())
            .addImport("static com.example.MyNumber.valueOf")
            .addEdit(Refactor.createReplaceMethodCallEdit(match.getElement().getCompilationUnit(), match.getOffset(), match.getLength(), "valueOf"));
    });
edit.apply();
