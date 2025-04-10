/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
console.log("hello picker.")

require.config({
  paths: {
    'xwiki-selectize': "$xwiki.getSkinFile('uicomponents/suggest/xwiki.selectize.js', true)" +
      "?v=$escapetool.url($xwiki.version)"
  }
});

define('xwiki-jira-suggestInstance', ['jquery', 'xwiki-selectize'], function($) {
  const jiraService = new XWiki.Document("JiraIssueCreationService", "JiraCode");
  const jiraParameters = {
    outputSyntax: "plain",
    action: "suggestInstance"
  };
  var getSettings = function(select) {
    return {
      load: function(typedText, callback) {
        $.getJSON(jiraService.getURL('get', $.param($.extend({}, jiraParameters))), {
          text: typedText
        }).done(callback).fail(callback);
      },
      loadSelected: function(selectedValue, callback) {
        $.getJSON(jiraService.getURL('get', $.param(jiraParameters)), {
          text: selectedValue,
          exactMatch: true
        }).done(callback).fail(callback);
      }
    };
  };

  $.fn.suggestJiraInstance = function(settings) {
    return this.each(function() {
      // TODO: Get instance from nearby selectize.
      const currentSelect = $(this);
      $(this).xwikiSelectize($.extend(getSettings($(this)), settings));
      currentSelect.on("change", function(event) {
        for (suggest of currentSelect.parents("form").find('.suggest-jira-project, .suggest-jira-issueType')) {
          suggest.selectize.clearOptions();
          suggest.selectize.onSearchChange();
        }
      });
    });
  };
});

define('xwiki-jira-suggestJiraProject', ['jquery', 'xwiki-selectize'], function($) {
  const jiraService = new XWiki.Document("JiraIssueCreationService", "JiraCode");

  var getSettings = function(select, getInstance) {
    return {
      load: function(typedText, callback) {
        this.loadedSearches = {}
        this.renderCache = {}
        const jiraParameters = {
          outputSyntax: "plain",
          instanceId: getInstance(),
          action: "suggestProject"
        };
        $.getJSON(jiraService.getURL('get', $.param($.extend({}, jiraParameters))), {
          text: typedText
        }).done(callback).fail(callback);
      },
      loadSelected: function(selectedValue, callback) {
        this.loadedSearches = {}
        this.renderCache = {}
        const jiraParameters = {
          outputSyntax: "plain",
          instanceId: getInstance(),
          action: "suggestProject"
        };
        $.getJSON(jiraService.getURL('get', $.param(jiraParameters)), {
          text: selectedValue,
          exactMatch: true
        }).done(callback).fail(callback);
      }
    };
  };

  $.fn.suggestJiraProject = function(settings) {
    return this.each(function() {
      const currentSelect = $(this);
      const getInstance = function() {
        return currentSelect.parents("form").find('.suggest-jira-instance').val();
      };
      currentSelect.xwikiSelectize($.extend(getSettings($(this), getInstance), settings));
      currentSelect.on("change", function(event) {
        for (suggest of currentSelect.parents("form").find('.suggest-jira-issueType')) {
          suggest.selectize.clearOptions();
          suggest.selectize.onSearchChange();
        }
      });
    });
  };
});

define('xwiki-jira-suggestJiraIssueType', ['jquery', 'xwiki-selectize'], function($) {
  const jiraService = new XWiki.Document("JiraIssueCreationService", "JiraCode");
  var getSettings = function(select, getInstance, getProject) {
    return {
      load: function(typedText, callback) {
        const jiraParameters = {
          outputSyntax: "plain",
          instanceId: getInstance(),
          project: getProject(),
          action: "suggestIssueType"
        };
        $.getJSON(jiraService.getURL('get', $.param($.extend({}, jiraParameters))), {
          text: typedText
        }).done(callback).fail(callback);
      },
      loadSelected: function(selectedValue, callback) {
        const jiraParameters = {
          outputSyntax: "plain",
          instanceId: getInstance(),
          project: getProject(),
          action: "suggestIssueType"
        };
        $.getJSON(jiraService.getURL('get', $.param(jiraParameters)), {
          text: selectedValue,
          exactMatch: true
        }).done(callback).fail(callback);
      }
    };
  };

  $.fn.suggestJiraIssueType = function(settings) {
    return this.each(function() {
      // TODO: Get instance and project from nearby selectize.
      const currentSelect = $(this);
      const getInstance = function() {
        return currentSelect.parents("form").find('.suggest-jira-instance').val();
      };
      const getProject = function() {
        return currentSelect.parents("form").find('.suggest-jira-project').val();
      };
      $(this).xwikiSelectize($.extend(getSettings($(this), getInstance, getProject), settings));
    });
  };
});

define('xwiki-jira-suggests', ['xwiki-jira-suggestInstance', 'xwiki-jira-suggestJiraProject', 'xwiki-jira-suggestJiraIssueType'], function() {

});

require(['jquery', 'xwiki-jira-suggests'], function($) {
  const createIssueCreationForm = function(textarea, tab) {
    // Remove existing form if it already exists
    $('#issueCreationForm').remove();

    const formTemplate = `
      <form id="issueCreationForm">

          <div class="form-group">
            <label for="instance">Instance</label>
            <select class="suggest-jira-instance">
              <option value="" id="instance">Select the instance</option>
            </select>
          </div>

          <div class="form-group">
            <label for="projectName">Project</label>
            <select class="suggest-jira-project" id="projectName">
              <option value="">Select the project</option>
            </select>
          </div>

          <div class="form-group">
            <label for="issueType">Issue Type</label>
            <select class="suggest-jira-issueType" id="issueType">
              <option value="">Select the issue type</option>
            </select>
          </div>

          <div class="form-group">
              <label for="issueSummary">Summary</label>
              <input type="text" class="form-control" id="issueSummary" placeholder="Enter title">
          </div>

          <div class="form-group">
              <label for="issueDescription">Description</label>
              <textarea class="form-control" id="issueDescription" rows="3" placeholder="Enter description"></textarea>
          </div>
      </form>
      <div>
        <p class="btn btn-primary jira-create-btn" id="createIssueBtn">Create Issue</p>
      </div>`
      ;

    // Append form to Tab.
    tab.append(formTemplate);

    // Handle create button click
    $('#createIssueBtn').on('click', function () {
        let projectName = $('#projectName').val().trim();
        let issueSummary = $('#issueSummary').val().trim();
        let issueDescription = $('#issueDescription').val().trim();

        if (projectName && issueSummary && issueDescription) {
            console.log('Issue Created:', { projectName:projectName, issueSummary:issueSummary, issueDescription:issueDescription });
            new XWiki.widgets.Notification('Created issue HEL-24: ' + issueSummary, 'done');
            textarea.value = "HEL-24\n" + textarea.value;
        } else {
            alert('All fields are required.');
        }
    });

    // Register our suggests
    $('.suggest-jira-instance').suggestJiraInstance();
    $('.suggest-jira-project').suggestJiraProject();
    $('.suggest-jira-issueType').suggestJiraIssueType();
  }

  const attachServerPicker = function(event, data) {
    let container = $((data && data.elements) || document);

    container.find(".macro-parameter[data-type='org.xwiki.contrib.jira.config.JIRAServer']").each(function () {
      $(this).find("select").suggestJiraInstance();
    });
  }

  const attachContentPicker = function(event, data) {
    let container = $((data && data.elements) || document);

    console.log("Attaching pickers.");
    container.find(".macro-parameter[data-type='org.xwiki.contrib.jira.macro.JIRAIssuesList']").each(function () {

      console.log("Attaching.");
      console.log($(this));

      const field = $(this).find('.macro-parameter-field').addClass("macro-parameter-group");

      const oldContent = field.children().clone(true)
      const contentNav = `
        <ul class="nav nav-tabs">
          <li role="presentation" class="active"><a href="#content-tab-list" role="tab" data-toggle="tab">Issues list</a></li>
          <li role="presentation" ><a href="#content-tab-new" role="tab" data-toggle="tab">New issue</a></li>
        </ul>
        <div class="tab-content">
          <div id="content-tab-list" role="tabpanel" class="tab-pane active macro-content-pane" >
          </div>
          <div id="content-tab-new" role="tabpanel" class="tab-pane">
          </div>
        </div>
      `
      field.empty().append(contentNav);
      field.find(".macro-content-pane").append(oldContent);

      const tab = field.find('#content-tab-new')
      const textarea = field.find('textarea[name="$content"]');
      console.log(textarea);
      createIssueCreationForm(textarea, tab);
    })
  }

  $(document).on('xwiki:dom:updated', attachContentPicker);
  $(document).on('xwiki:dom:updated', attachServerPicker);
  attachContentPicker();
  attachServerPicker();
});
