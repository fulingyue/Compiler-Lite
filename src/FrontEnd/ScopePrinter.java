package FrontEnd;

import FrontEnd.Scope.Scope;

public class ScopePrinter extends ScopeBuilder {
    public void printScopeTree(Scope globalScope) {
        globalScope.printScope(0);
    }
}
