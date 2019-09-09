// Agent sample_agent in project ts_ap

/* Initial beliefs and rules */
e.
/* Initial goals */
!z.
+e <- !g.

/* Plans */
// +!g <- !update_ap_g; !g_ap.
//
// +!update(U) <- active_perception.update(U).
//
// +!update_ap_g
// 	<- 	!update(b[ap(1000)]) |&|
// 		!update(c[ap(2000)]) |&|
// 		!update(d[ap(3000)]).

+!z <- .print("teste").

+!g[ap(8888)] <- .print("oi").

+!g: b[ap(1000)] & c[ap(2000)] & d[ap(3000)]
	<- .print("GOAL G1").

+!g: b[ap(1000)] & c[ap(2000)]
	<- .print("GOAL G2").

+!g: b[ap(1000)]
	<- .print("GOAL G3").

+!g: h <- .print("GOAL G4!").

+!g <- .print("GOAL G5!").

+?b[ap(T)]
	<-	.time(HH,MM,SS);
		+b[ap(T),lu(HH,MM,SS)];
		.print("Active perception plan for b").

+?c[ap(T)]
	<-	.time(HH,MM,SS);
		+c[ap(T),lu(HH,MM,SS)];
		.print("Active perception plan for c").

+?d[ap(T)] <- .print("Active perception plan for d").

{apply_ap("oi!!")}
