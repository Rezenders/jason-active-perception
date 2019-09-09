// Agent sample_agent in project ts_ap

/* Initial beliefs and rules */
e.
/* Initial goals */
+e <- !g.

/* Plans */

+!g: b[ap, 1000] & c[ap, 2000] | d[ap,3000]
	<- .print("GOAL G1").

+!g: b[ap, 1000] & c[ap, 2000]
	<- .print("GOAL G1").

+!g: b[ap, 1000]
	<- .print("GOAL G2").

+!g <- .print("GOAL G3!").

+?b[ap]: true
	<-	+b[lu(.time())];
		.print("Active perception plan for b").

+?c[ap] <- .print("Active perception plan for c").
