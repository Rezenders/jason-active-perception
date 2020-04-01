// 2 & Beliefs & Grouped & All & Time & Natural

e.
+e <- !g[ap].

+!g[ap]
	<- 	!update(b[ap(1000)])[ap];
			!update(c[ap(2000)])[ap];
			!update(d[ap(3000)])[ap];
			!g[rp].

+!g[rp]: b[ap(1000)] & c[ap(2000)] & d[ap(3000)]
	<-	.print("GOAL G1").

+!g[rp]: b[ap(1000)] & c[ap(2000)]
	<-	.print("GOAL G2").

+!g[rp] <- .print("GOAL G3").

-!g[rp] <- .print("Falhou").

+?b[ap(T)]
	<-	.time(HH,MM,SS,MS);
		+b[ap(T),lu(HH,MM,SS,MS)];
		.print("Active perception plan for b").

+?c[ap(T)]
	<-	.time(HH,MM,SS,MS);
		+c[ap(T),lu(HH,MM,SS,MS)];
		.print("Active perception plan for c").

+?d[ap(T)] <- .print("Active perception plan for d").

+!update(X)[ap]: not active_perception.isUpdated(X)
    <- ?X.

+!update(X)[ap].

//// +!g: b[ap(1000)] & c[ap(2000)] & d[ap(3000)]
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
