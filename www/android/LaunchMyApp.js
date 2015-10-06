(function () {
    "use strict";

    var remainingAttempts = 10;

  function waitForAndCallHandlerFunction2(idata) {
    if (typeof window.handleNewIntentINIT === "function") {
        window.handleNewIntentINIT(idata);
      // Clear the intent when we have a handler
      //cordova.exec(null, null, "LaunchMyApp", "clearIntent", []);
    } else if (remainingAttempts-- > 0) {
      setTimeout(function(){waitForAndCallHandlerFunction2(idata);}, 500);
    }
  }

  function triggerShareOpenURL() {
    cordova.exec(waitForAndCallHandlerFunction2, null, "LaunchMyApp", null, []);
  }
  
  document.addEventListener("deviceready", triggerShareOpenURL, false);
}());
