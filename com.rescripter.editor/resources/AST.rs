var AST = function() { }

AST.parseCompilationUnit = function(cu) {
    var parser = org.eclipse.jdt.core.dom.ASTParser.newParser(org.eclipse.jdt.core.dom.AST.JLS3);
    parser.setKind(org.eclipse.jdt.core.dom.ASTParser.K_COMPILATION_UNIT);
    parser.setSource(cu);
    parser.setResolveBindings(true);
    
    return parser.createAST(null);
}

AST.findCoveredNode = function(ast, offset, length) {
    return org.eclipse.jdt.core.dom.NodeFinder.perform(ast, offset, length);
}
