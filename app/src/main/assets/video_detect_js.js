function resetConsole() {
    var iframe = document.createElement('iframe');
    iframe.style.display = 'none';
    document.body.appendChild(iframe);
    console = iframe.contentWindow.console;
    window.console = console;
}
resetConsole();

var isPlayOnWeb=false;
var interval = setInterval(function videoTagFinder() {
    console.info("start detect....")
    detectVideoElements(document, null)
    var videoFrames = document.getElementsByTagName("iframe");
    for (var index = 0; index < videoFrames.length; index++) {
        var frameTag = videoFrames[index];
        console.info("frame: " + frameTag.src);
        if (frameTag.getAttribute("marked") =="1") {
            continue;
        }
        console.error("handle frame: " + frameTag.src);
        if (frameTag.src != null) {
            console.error("frame already loaded: " + frameTag.src);
            try { onFrameLoaded(frameTag) } catch(e) { console.error(e); }
        }
//         else {
//            frameTag.onLoad = function() {
//                console.error("frame loaded: " + frameTag.src);
//                onFrameLoaded(frameTag)
//            }
//        }
        frameTag.setAttribute("marked","1");
    }

    function detectVideoElements(doc) {
        var videoTags = doc.getElementsByTagName("video");
        for (var index =0; index < videoTags.length; index++) {
            var videoTag = videoTags[index];
            if (videoTag.getAttribute("marked") =="1") {
                continue;
            }
            videoTag.addEventListener("webkitbeginfullscreen", onEnterFullScreenEvent,true);
            videoTag.addEventListener("webkitendfullscreen", onExitFullScreenEvent,true);
            videoTag.addEventListener("play", onVideoPlayEvent, true);
            videoTag.addEventListener("pause", onVideoPauseEvent,true);
            videoTag.setAttribute("position",index);
            // 标记一下，防止重复设置
            videoTag.setAttribute("marked","1");
        }
    }

    function onEnterFullScreenEvent() { }
    function onExitFullScreenEvent() { }
    function onVideoPlayEvent() {
       if(!isPlayOnWeb) {
           checkIsNeedUsePlayerOfApp(this)
       }
    }
    function onVideoPauseEvent() { }

    function checkIsNeedUsePlayerOfApp(videoTag) {
        var i = setInterval(function () {
            notifyAppPlayerToPlay(videoTag);
            // 交给本地，web不播放了
            videoTag.pause();
            clearInterval(i)
        }, 200)
    }
    function getVideoInfo(video) {
       var rect = video.getBoundingClientRect();
       return {
          'scrollHeight': document.body.scrollHeight,
          'width': rect.width,
          'height': rect.height,
          'left': rect.left,
          'top': rect.top + window.scrollY,
          'url': video.src,
          'poster': video.poster
       };
  }
    /**
    * notify app to play video
    * @param videoTag
    */
    function notifyAppPlayerToPlay(videoTag) {
        var videoInfo = getVideoInfo(videoTag);
        // 调用native方法
        window.JSToNative.webViewPlayVideoAtURL(JSON.stringify(videoInfo));
    }

    function onFrameLoaded(frameTag) {
        if (frameTag.src.indexOf(".m3u8") != -1) {
            var rect = frameTag.getBoundingClientRect();
            var info = {
              'scrollHeight': document.body.scrollHeight,
              'width': rect.width,
              'height': rect.height,
              'left': rect.left,
              'top': rect.top + window.scrollY,
              'url': frameTag.src,
              'poster': ""
           };
//           window.JSToNative.webViewPlayVideoAtURL(JSON.stringify(videoInfo));
           console.error(JSON.stringify(info));
        }
    }
}, 300)

function setPlayOnWeb() {
    isPlayOnWeb=true;
}
