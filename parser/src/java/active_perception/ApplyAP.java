package active_perception;

import jason.asSemantics.Agent;
import jason.asSyntax.*;
import jason.asSyntax.directives.*;
import jason.asSyntax.BodyLiteral.BodyType;

public class ApplyAP extends DefaultDirective implements Directive {

	public Agent process(Pred directive, Agent outerContent, Agent innerContent) {
		System.out.println(outerContent.getPL());
		if (outerContent == null)
            return null;
		// Agent newAg = new Agent();
		return null;
	}

}
