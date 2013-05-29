+ SimpleNumber {
    // midi interval to rq (reciprocal of Q factor)
    // useful for specifying bandwidth as midi interval
    // 12 is one octave
	midirq { 
    	var x = 2**(this / 24.0); 
    	^(x*x - 1) / x; 
	} 


}
