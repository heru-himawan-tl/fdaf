/*
 * Copyright (c) Heru Himawan Tejo Laksono. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

var mobileMenuIsOpened = false;

function setFakeFileInputValue(object, classId) {
    if (object.files && object.files[0]) {
        document.querySelector('.'+classId).value=object.files[0].name;
    }   
}

function fireInputFile(classId) {
    document.querySelector('.'+classId).click(); 
}

var systemStateReloadFlag = 0;
var prepareReloadHandle = null;
var systemStateReloadDone = 1;

function execPrepareReload(webappURL, imgEID, dialogContainerSID, messageBoxSID, reloadMessage, reloadingPageMessage) {
    systemStateReloadDone = 0;
    prepareReloadHandle = setInterval(function() {
        var mbx = document.querySelector("." + messageBoxSID);
        var img = document.getElementById(imgEID);
        var dcx = document.querySelector("." + dialogContainerSID);
        if (dcx != null) {
            dcx.style.cssText='display: -webkit-box; display: -moz-box; display: -ms-flexbox; display: -webkit-flex; display: flex;';
        }
        if (systemStateReloadDone > 0) {
            return;
        }
        if (mbx != null) {
            if (mbx.style.display == 'block') {
                mbx.style.display = 'none';
            } else {
                mbx.style.display = 'block';
            }
            mbx.innerHTML = reloadMessage;
        }
        if (img != null) {
            img.onload = function() {
                if (systemStateReloadFlag > 0) {
                    systemStateReloadDone = 1;
                    mbx.style.display = 'block';
                    mbx.innerHTML = reloadingPageMessage;
                    location.reload(true);
                    clearInterval(prepareReloadHandle);
                    prepareReloadHandle = null;
                }
            }
            img.onerror = function () {
                systemStateReloadFlag++;
            }
            img.src = webappURL + "/web-resources/network-test.png?r=" + Math.round((new Date()).getTime() / 1000);
        }
        
    }, 1000);
}

var networkStateInHandled = false;

var connectionCheckHandle = null;
var connectionFound = false;
var connectionLost  = false;

function connectionCheck(webappURL, imgEID, dialogContainerSID, messageBoxSID) {
    connectionFound = false;
    connectionLost  = false;
    connectionCheckHandle = setInterval(function() {
        if (networkStateInHandled) {
            clearInterval(connectionCheckHandle);
            connectionCheckHandle = null;
        }
        var dcx = document.querySelector("." + dialogContainerSID);
        var mbx = document.querySelector("." + messageBoxSID);
        var img = document.getElementById(imgEID);
        if (connectionFound) {
            if (dcx != null) {
                dcx.style.display = 'none';
            }
            connectionFound = false;
            connectionLost = false;
            location.reload(true);
            clearInterval(connectionCheckHandle);
            connectionCheckHandle = null;
            return;
        }
        if (img != null) {
            img.onload = function() {
                if (connectionLost) {
                    if (mbx != null) {
                        mbx.style.display = 'block';
                        mbx.innerHTML = "Web application connection return ...";
                    }
                    connectionFound = true;
                    connectionLost = false;
                }
            }
            img.onerror = function () {
                if (connectionLost == false) {
                    if (dcx != null) {
                        dcx.style.cssText='display: -webkit-box; display: -moz-box; display: -ms-flexbox; display: -webkit-flex; display: flex;';
                    }
                    if (mbx != null) {
                        mbx.style.display = 'block';
                        mbx.innerHTML = "Web application connection lost ...";
                    }
                    connectionLost = true;
                }
            }
            img.src = webappURL + "/web-resources/network-test.png?r=" + Math.round((new Date()).getTime() / 1000);
        }
        if (connectionLost) {
            if (mbx != null) {
                if (mbx.style.display == 'block') {
                    mbx.style.display = 'none';
                } else {
                    mbx.style.display = 'block';
                }
                mbx.innerHTML = "Web application connection lost ...";
            }
        }
    }, 1000);
}

var prepareRedirectHandle = null;
var prepareRedirectDone = 1;

function execPrepareRedirect(webappURL, imgEID, dialogContainerSID, messageBoxSID, newIndexURL, networkStateChangeMessage, redirectMessage) {
    var mbx = document.querySelector("." + messageBoxSID);
    var img = document.getElementById(imgEID);
    prepareRedirectDone = 0;
    prepareRedirectHandle = setInterval(function() {
        var dcx = document.querySelector("." + dialogContainerSID);
        if (dcx != null) {
            dcx.style.cssText='display: -webkit-box; display: -moz-box; display: -ms-flexbox; display: -webkit-flex; display: flex;';
        }
        if (prepareRedirectDone > 0) {
            if (mbx != null) {
                mbx.style.display = 'block';
                mbx.innerHTML = redirectMessage;
            }
            networkStateInHandled = false;
            document.location.href = newIndexURL;
            clearInterval(prepareRedirectHandle);
            prepareRedirectHandle = null;
            return;
        }
        if (mbx != null) {
            if (mbx.style.display == 'block') {
                mbx.style.display = 'none';
            } else {
                mbx.style.display = 'block';
            }
            mbx.innerHTML = networkStateChangeMessage;
        }
        if (img != null) {
            img.onload = function() {
                prepareRedirectDone++;
            }
            img.onerror = function () {
            }
            img.src = newIndexURL + "/web-resources/network-test.png?r=" + Math.round((new Date()).getTime() / 1000);
        }
        
    }, 1000);
}

function initNetworkStateWS(URI, webappURL, imgEID, dialogContainerSID, messageBoxSID, serviceUUID, networkStateChangeMessage, redirectMessage) {
    var networkStateWS = new WebSocket(URI);
    networkStateWS.onmessage = function (event) {
        var dc = document.querySelector('.' + dialogContainerSID);
        var mb = document.querySelector('.' + messageBoxSID);
        if (dc != null && mb != null) {
            var data = event.data;
            if (data.search(",")) {
                var a = data.split(",");
                if (a[0] == serviceUUID && prepareRedirectHandle == null) {
                    networkStateInHandled = true;
                    execPrepareRedirect(webappURL, imgEID, dialogContainerSID, messageBoxSID, a[1], networkStateChangeMessage, redirectMessage);
                }
            } else {
                if (data == serviceUUID && prepareRedirectHandle != null) {
                    dc.style.display = "none";
                    prepareRedirectDone = 1;
                    networkStateInHandled = false;
                    clearInterval(prepareRedirectHandle);
                }
            }
        }
    }
    networkStateWS.onopen = function (event) {
    }
}

function initRealtimeClockWS(URI, selectors) {
    var realtimeClockWS = new WebSocket(URI);
    realtimeClockWS.onmessage = function (event) {
        /*var etc = document.querySelector(".schedule_legent_flex_box");
        if (etc != null) {
            if (etc.classList.contains("etc_hiden_box")) {
                etc.classList.remove("etc_hiden_box");
            } else {
                etc.classList.add("etc_hiden_box");
            }
        }*/
        for (var i = 0; i < selectors.length; i++) {
            var e = document.querySelector("." + selectors[i]);
            if (e != null) {
                e.innerHTML = event.data;
            }
        }
    }
    realtimeClockWS.onopen = function (event) {
    }
}


