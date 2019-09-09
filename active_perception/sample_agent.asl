// Agent sample_agent in project active_perception

/* Initial beliefs and rules */
// broken.
/* Initial goals */
!fly.

/* Plans */
// +!fly: true
// 	<-	active_perception.reveal_concurrent(allowed_to_fly, verify_status, 10000);
// 		.print("Takeoff").

+!fly: active_perception.reveal_brute(allowed_to_fly, verify_status)
	<-	.print("Takeoff").
//
// +!fly: .print("oi")
// 	<-	.print("Take").

+!verify_status: not broken
	<-	.print("status");
		+allowed_to_fly.

+!verify_status: broken
	<-	-allowed_to_fly.
