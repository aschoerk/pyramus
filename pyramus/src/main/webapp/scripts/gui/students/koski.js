
function loadLogEntries(personId) {
  JSONRequest.request("students/koskipersonstate.json", {
    parameters : {
      personId : personId
    },
    onSuccess : function(jsonResponse) {
      $('koski-status-details').empty();
      var logEntries = jsonResponse.logEntries;
      var status = jsonResponse.koskiStatus;
      if (logEntries) {
        for (var i = 0, l = logEntries.length; i < l; i++) {
          var m = moment(logEntries[i].date);
          var dateStr = getLocale().getDate(logEntries[i].date, false);
          var timeStr = m.format("H:mm");
          var studyProgrammeName = logEntries[i].studyProgrammeName ? " (" + logEntries[i].studyProgrammeName + ")" : "";
          var message = logEntries[i].message ? " (" + logEntries[i].message + ")" : "";
          var text = dateStr + " " + timeStr + " " + logEntries[i].text + message + studyProgrammeName;
          var detailclass = "koski-details-"
              + logEntries[i].stateType.toLowerCase();
          var e = new Element("div", {
            className : detailclass
          }).update(text);
          $('koski-status-details').insert(e);
        }
      }
      $('koski-status').addClassName('koski-' + status.toLowerCase());
    },
    onFailure: function() {
      // Not that critical to do anything
    }
  });
}

function toggleKoskiLogDetailsVisibility(event) {
  Event.stop(event);
  var element = $('koski-status-details');
  if (element.visible()) {
    element.hide();
  } else {
    element.show();
  }
}
      
