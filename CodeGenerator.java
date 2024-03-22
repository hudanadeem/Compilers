import absyn.*;

public class CodeGenerator implements AbsynVisitor {
	int mainEntry, globalOffset;

	// add constructor and all emitting routines

	public void visit(Absyn trees) {	// wrapper for post-order traversal
		// generate the prelude

		// generate the i/o routines

		// make a request to the visit method for DecList
		trees.accept(this, 0, false);

		// generate finale

	}

	// implement all visit method in AbsynVisitor such as the following
	public void visit(DecList decs, int offset, Boolean isAddress) {
		
	}
}