package xyz.dean.androiddemos.demos.webvideo2native

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

object WebVideo2NativeHelper {
    val GET_VIDEO_INFO_SCRIPT = """
        |const postVideoPlay = () => {
        |  function getVideoInfo() {
        |    let video = videoRef.value;
        |    var rect = video.getBoundingClientRect();
        |    return {
        |      'scrollHeight': document.body.scrollHeight,
        |      'width': rect.width,
        |      'height': rect.height,
        |      'left': rect.left,
        |      'top': rect.top + window.scrollY,
        |      'url': video.src,
        |      'poster': video.poster
        |    };
        |  }
        |  // 调用约定好的ydk方法
        |  window.ydk.onVideoPlay(getVideoInfo())
        |}
        |
        |videoRef.value.addEventListener('play', postVideoPlay);
    """.trimMargin()

    val VIDEO_DETECT_SCRIPT = """
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
              'url': extractVideoURL(frameTag.src),
              'poster': ""
           };
           window.JSToNative.webViewPlayVideoAtURL(JSON.stringify(info));
           console.error(JSON.stringify(info));
           frameTag.remove();
        }
    }
}, 300)

function setPlayOnWeb() {
    isPlayOnWeb=true;
}

function extractVideoURL(fullURL) {
    const url = new URL(fullURL);
    const videoURLParam = url.searchParams.get('url');
    return videoURLParam || 'No video URL found';
}
    """.trimMargin()
}

// {"scrollHeight":1480,"width":393.0909118652344,"height":221.0625,"left":0,"top":63,"url":"https://media001.geekbang.org/defe0fd07d4d71ee803c6732b68f0102/9c6a33b567a13c246cd8fa8497ce3f67-fd-encrypt-stream.m3u8?MtsHlsUriToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb2RlIjoiNzA0NDYyMDJiYzExN2EwZGY2ZjE2MGQzZGQyMGE1YzQ5M2Q1NDQ0MSIsImV4cCI6MTcwMzUwNTQ4MjM5MCwiZXh0cmEiOnsidmlkIjoiZGVmZTBmZDA3ZDRkNzFlZTgwM2M2NzMyYjY4ZjAxMDIiLCJhaWQiOjcyMzUxOSwidWlkIjowLCJzIjoiIn0sInMiOjIsInQiOjEsInYiOjF9.M_lV1-U1526hW1rbNFRuEbcceQbynriAou5UR5T2ASg","poster":"https://media001.geekbang.org/b42e93a07d4971ee80695017f0f80102/snapshots/5bc97369df7a4c1aa4ad5f93ae704be0-00005.jpg"}
@Keep
data class VideoInfo(
    @SerializedName("scrollHeight") val scrollHeight: Double,
    @SerializedName("width") val width: Double,
    @SerializedName("height") val height: Double,
    @SerializedName("left") val left: Double,
    @SerializedName("top") val top: Double,
    @SerializedName("url") val url: String,
    @SerializedName("poster") val poster: String
)