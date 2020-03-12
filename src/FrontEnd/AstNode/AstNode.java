package FrontEnd.AstNode;

import FrontEnd.AstVisitor;
import FrontEnd.Scope.Scope;
import util.Location;

public abstract class AstNode {
    protected Location location;
    protected AstNode parent;
    protected Scope scope;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public AstNode getParent() {
        return parent;
    }

    public void setParent(AstNode parent) {
        this.parent = parent;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }



    public void getInfo(int tab){
        util.print_tool.printDashAndStr(tab, this.getClass().getName());

    }

    abstract public void accept(AstVisitor vistor) throws Exception;


}
