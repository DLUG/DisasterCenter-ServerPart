function JobTreeNode(initValue){
//	console.log("New: " + initValue.timeline);
	this.jobTicket = initValue;
	this.left = null;
	this.right = null;
	this.put = function(jobTicket){
		var thisTimeline = this.jobTicket.timeline;
		var targetTimeline = jobTicket.timeline;
		
//		console.log("put: " + thisTimeline + ", target: " + targetTimeline);
		
		if(thisTimeline <= targetTimeline){
			if(this.right == null){
//				console.log("putted");
				this.right = new JobTreeNode(jobTicket);
			} else {
				this.right.put(jobTicket);
			}
		} else {
			if(this.left == null){
//				console.log("putted");
				this.left = new JobTreeNode(jobTicket);
			} else {
				this.left.put(jobTicket);
			}
		}
	};
	
	this.get = function(timeline){
		var thisTimeline = this.jobTicket.timeline; 
//		console.log("get: " + thisTimeline + ", target: " + timeline + ", isused: " + this.jobTicket.isused);
		
		if(thisTimeline == timeline && !this.jobTicket.isused){
//			console.log("getted1");
			this.jobTicket.isused = true;
			return this.jobTicket;
		}else {
			var getTarget = null;
			if(thisTimeline <= timeline)
				getTarget = this.right;
			else
				getTarget = this.left;
			
			if(getTarget == null){
				if(this.jobTicket.isused)
					return null;
//				console.log("getted2");
				this.jobTicket.isused = true;
				return this.jobTicket;
			}else {
				var tmpResult = getTarget.get(timeline);
				if(tmpResult){
					return tmpResult
				} else {
					if(this.jobTicket.isused)
						return null;
//					console.log("getted3");
					this.jobTicket.isused = true;
					return this.jobTicket;
				}
			}
				
		}
	};
}

function JobTicket(timeline){
	this.timeline = timeline;
	this.jobIdx = null;
	this.jobAction = null;
	this.isused = false;
	this.timeout = 0;
}

var PinAnimator = new Object();
PinAnimator.ANICODE_HIDE_ANIMATION = 0;
PinAnimator.ANICODE_HIDE = 1;
PinAnimator.ANICODE_SHOW_ANIMATION = 2;
var initJobTicket = new JobTicket(0);
initJobTicket.isused = true;
PinAnimator.data = new JobTreeNode(initJobTicket);
//PinAnimator.regTimeline = 0;
PinAnimator.playedTimeline = 0;
PinAnimator.pinArr = new Array();
PinAnimator.addAni = function(idx, actionCode, timeout){
	var thisTimeline = (this.playedTimeline + timeout);
	
	var tmpJobTicket = new JobTicket(thisTimeline);
	tmpJobTicket.jobIdx = idx;
	tmpJobTicket.jobAction = actionCode;
	tmpJobTicket.timeout = timeout;
	
	this.data.put(tmpJobTicket);
	
	setTimeout(function(){
		 
		var thisTimeline = PinAnimator.playedTimeline;
		var thisJobTicket = PinAnimator.data.get(thisTimeline);
		
//		console.log("playing: " + thisJobTicket.timeline);
		if((thisJobTicket.timeline) > PinAnimator.playedTimeline)
			PinAnimator.playedTimeline = (thisJobTicket.timeline);
		
		var jobAction = thisJobTicket.jobAction;
		var jobIdx = thisJobTicket.jobIdx;
		
		if(jobAction == PinAnimator.ANICODE_HIDE_ANIMATION){
			PinAnimator.pinArr[jobIdx].setAnimation(google.maps.Animation.BOUNCE);
		} else if(jobAction == PinAnimator.ANICODE_HIDE){
			PinAnimator.pinArr[jobIdx].setVisible(false);
		} else if(jobAction == PinAnimator.ANICODE_SHOW_ANIMATION){
			PinAnimator.pinArr[jobIdx].setAnimation(google.maps.Animation.DROP);
			PinAnimator.pinArr[jobIdx].setVisible(true);
		}
	}, timeout);
};