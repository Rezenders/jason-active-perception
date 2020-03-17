e.
+e <- !g.

+!g: b[ap(1000)] & c[ap(2000)] & d[ap(3000)]
	<-	.print("GOAL G1").

+!g: b[ap(1000)] & c[ap(2000)]
	<-	.print("GOAL G2").

+!g <- .print("GOAL G3").

-!g <- .print("Falhou").

+?b[ap(T)]
	<-	.time(HH,MM,SS,MS);
		+b[ap(T),lu(HH,MM,SS,MS)];
		.print("Active perception plan for b").

+?c[ap(T)]
	<-	.time(HH,MM,SS,MS);
		+c[ap(T),lu(HH,MM,SS,MS)];
		.print("Active perception plan for c").

+?d[ap(T)] <- .print("Active perception plan for d").
