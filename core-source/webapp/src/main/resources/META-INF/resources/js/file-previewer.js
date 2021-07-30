
var procResizeIframeCount = 0;
var procResizeIframeIntrv = null;

function procResizeIframe() {
    var h = document.body.scrollHeight;
    if (h > 0) {
        var fpfb = parent.document.getElementsByClassName('file_previewer_frame_box')[0];
        if (fpfb != null) {
            var pb = window.getComputedStyle(fpfb, null).getPropertyValue('padding-bottom').replaceAll(/[a-zA-Z ]+/ig, '');
            var pt = window.getComputedStyle(fpfb, null).getPropertyValue('padding-top').replaceAll(/[a-zA-Z ]+/ig, '');
            fpfb.style = 'height:' + (h + parseInt(pb) + parseInt(pt) + 2) + 'px';
        }
    }
}

function resizeIframe() {
    var mv = document.getElementById('FilePreviewToolVideo');
    if (mv != null) {
        mv.addEventListener('canplay', (event) => {
            procResizeIframe();
        });
        return;
    }
    procResizeIframe();
    procResizeIframeIntrv = setInterval(function () {
        if (procResizeIframeCount >= 20) {
            clearInterval(procResizeIframeIntrv);
            procResizeIframeIntrv = null;
            return;
        }
        procResizeIframe();
        procResizeIframeCount++;
    }, 50);
}

