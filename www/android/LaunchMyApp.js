(function () {
    "use strict";

    var remainingAttempts = 10;

  function waitForAndCallHandlerFunction2(itype, url) {
    if (typeof window.handleNewIntent === "function") {
        window.handleNewIntent(itype, url);
      // Clear the intent when we have a handler
      cordova.exec(null, null, "LaunchMyApp2", "clearIntent", []);
    } else if (remainingAttempts-- > 0) {
      setTimeout(function(){waitForAndCallHandlerFunction2(itype ,url);}, 500);
    }
  }

  function triggerShareOpenURL() {
    cordova.exec(waitForAndCallHandlerFunction2, null, "LaunchMyApp2", "checkIntent", []);
  }
  
  document.addEventListener("deviceready", triggerShareOpenURL, false);
}());
