// 3 & Beliefs & Individual & All & Time & Natural

e.
+e <- !g.

// +!g: b[ap(1000)] & c[ap(2000)] & d[ap(3000)]
// 	<- .print("GOAL G1").
//
// +!g: b[ap(1000)] & c[ap(2000)]
// 	<- .print("GOAL G2").
//
// +!g: b[ap(1000)]
// 	<- .print("GOAL G3").
//
// +!g: h <- .print("GOAL G4!").
//
// +!g <- .print("GOAL G5!").
//
// -!g <- .print("Falhou").

+!g <- 	active_perception.update(b[ap(1000)]);
		active_perception.update(c[ap(2000)]);
		active_perception.update(d[ap(3000)]);
		!g[ap,l_1].

+!g <- 	active_perception.update(b[ap(1000)]);
		active_perception.update(c[ap(2000)]);
		!g[ap,l_2].

+!g <- !g[ap, l_5].

-!g.

+!g[ap,l_1]: b[ap(1000)] & c[ap(2000)] & d[ap(3000)]
	<-	.print("GOAL G1").

+!g[ap,l_2]: b[ap(1000)] & c[ap(2000)]
	<-	.print("GOAL G2").

+!g[ap,l_5] <- .print("GOAL G5").

-!g[ap,l_1] <- .print("Falhou").
-!g[ap,l_2] <- .print("Falhou").
-!g[ap,l_5] <- .print("Falhou").

+?b[ap(T)]
	<-	.time(HH,MM,SS,MS);
		+b[ap(T),lu(HH,MM,SS,MS)];
		.print("Active perception plan for b").

+?c[ap(T)]
	<-	.time(HH,MM,SS,MS);
		+c[ap(T),lu(HH,MM,SS,MS)];
		.print("Active perception plan for c").

+?d[ap(T)] <- .print("Active perception plan for d").
