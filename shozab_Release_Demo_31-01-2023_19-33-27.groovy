release 'Shozab Demo Release', {
  plannedEndDate = '2024-07-11T11:28'
  plannedStartDate = '2024-06-26T11:28'
  projectName = 'snaqvi Demo'
  timeZone = 'America/New_York'

  pipeline 'Template Release Workflow', {
    releaseName = 'Shozab Demo Release'
    templatePipelineName = 'Template Release Workflow'
    templatePipelineProjectName = 'POC'

    formalParameter 'ec_stagesToRun', {
      expansionDeferred = '1'
    }

    stage 'Release Readiness', {
      colorCode = '#00adee'
      duration = '11520'
      pipelineName = 'Template Release Workflow'
      plannedEndDate = '2021-08-10T00:00'
      plannedStartDate = '2021-08-02T00:00'
      gate 'POST', {
        task 'Code Coverage > 75', {
          gateCondition = '''$[/javascript 
  //myStageRuntime.tasks[\'Run Code Quality And Security Scan\'].job.getLastSonarMetrics.coverage]	> 75	]'''
          gateType = 'POST'
          subproject = 'snaqvi Demo'
          taskType = 'CONDITIONAL'

          // Custom properties
          subservice = ''
        }

        task 'No Critical Violations', {
          gateCondition = '''$[/javascript 
  myStageRuntime.tasks[
  \'Run Code Quality And Security Scan\'].job.getLastSonarMetrics.violations]	< 10	]'''
          gateType = 'POST'
          subproject = 'snaqvi Demo'
          taskType = 'CONDITIONAL'

          // Custom properties
          subservice = ''
        }
      }

      task 'Get Jira Tickets', {
        actualParameter = [
          'config': '/projects/Demo Onboarding/pluginConfigurations/cb-demos-jira',
          'createLink': '1',
          'fieldsToSave': '',
          'filter': '',
          'jql': 'Project = BT and summary !~ "Exemption requested by"',
          'maxResults': '',
          'resultFormat': 'propertySheet',
          'resultProperty': '/myJob/getIssuesResult',
        ]
        stageSummaryParameters = '[{"label":"JIRA IDs","name":"jiraids"},{"label":"JIRA Report","name":"jirareporturl"}]'
        subpluginKey = 'EC-JIRA'
        subprocedure = 'GetIssues'
        taskType = 'PLUGIN'

        // Custom properties
        subservice = ''
      }

      task 'Verify Jenkins Build Status', {
        actualParameter = [
          'build_number': '46',
          'config_name': '/projects/snaqvi Demo/pluginConfigurations/snaqvi',
          'job_name': 'Pet%20Store%20Pipeline',
          'need_to_run_report': '1',
          'result_outpp': '/myJobStep/buildDetails',
        ]
        enabled = '0'
        subpluginKey = 'EC-Jenkins'
        subprocedure = 'GetBuildDetails'
        taskType = 'PLUGIN'

        // Custom properties
        subservice = ''
      }

      task 'Quality Checks ', {
        taskType = 'GROUP'

        task 'Code Quality And Security Scan', {
          actualParameter = [
            'config': '/projects/Demo Onboarding/pluginConfigurations/cb-demos-sonar',
            'resultFormat': 'propertysheet',
            'resultSonarProperty': '/myJob/getLastSonarMetrics',
            'sonarMetricsComplexity': 'all',
            'sonarMetricsDocumentation': 'all',
            'sonarMetricsDuplications': 'all',
            'sonarMetricsIssues': 'all',
            'sonarMetricsMaintainability': 'all',
            'sonarMetricsMetrics': 'all',
            'sonarMetricsQualityGates': 'all',
            'sonarMetricsReliability': 'all',
            'sonarMetricsSecurity': 'all',
            'sonarMetricsTests': 'all',
            'sonarProjectKey': 'petclinic',
            'sonarProjectName': 'petclinic',
            'sonarProjectVersion': '2.2.0.BUILD-SNAPSHOT',
            'sonarTaskId': '',
            'sonarTimeout': '',
          ]
          groupName = 'Quality Checks '
          subpluginKey = 'EC-SonarQube'
          subprocedure = 'Get Last SonarQube Metrics'
          taskType = 'PLUGIN'

          // Custom properties
          subservice = ''
        }

        // Custom properties
        subservice = ''
      }
    }

    stage 'QA', {
      colorCode = '#ff7f0e'
      duration = '7200'
      pipelineName = 'Template Release Workflow'
      plannedEndDate = '2021-08-14T00:00'
      plannedStartDate = '2021-08-09T00:00'
      gate 'PRE', {
        task 'Wait for Release Manager Approval', {
          gateType = 'PRE'
          notificationEnabled = '1'
          notificationTemplate = 'ec_default_gate_task_notification_template'
          taskType = 'APPROVAL'
          approver = [
            'snaqvi',
          ]

          // Custom properties
          subservice = ''
        }
      }

      gate 'POST', {
        task '75% Passing QA Tests', {
          gateCondition = 'echo "Exit gate check for Testing..."'
          gateType = 'POST'
          subproject = 'snaqvi Demo'
          taskType = 'CONDITIONAL'

          // Custom properties
          subservice = ''
        }

        task 'No P1s & P2s Issues', {
          gateCondition = 'echo "Exit gate check for Jira for Sev1/Sev2..."'
          gateType = 'POST'
          subproject = 'snaqvi Demo'
          taskType = 'CONDITIONAL'

          // Custom properties
          subservice = ''
        }
      }

      task 'Deploy to QA', {
        deployerRunType = 'serial'
        enabled = '0'
        taskType = 'DEPLOYER'

        // Custom properties
        subservice = ''
      }

      task 'Run Functional Test', {
        command = '''echo "Call Selenium API for enabling Feature Flag experiment for QA env" 
  ectool setProperty \'/myStageRuntime/ec_summary/Test Report\' \'<HTML><a href="https://docs.google.com/spreadsheets/u/1/d/e/2PACX-1vQ2H8g-Y1omFLVyXDcmODlvrXY04D_Pf4r3ZQ9axP0YomUds0iSs5R05spQl9XRg6Ggmjgq6PW_pO4L/pubhtml?gid=368743228&single=true" target="_blank"> View Report </a> </html>\''''
        taskType = 'COMMAND'

        // Custom properties
        subservice = ''
      }

      task 'Verify QA - Notify', {
        enabled = '0'
        notificationEnabled = '1'
        notificationTemplate = 'ec_default_pipeline_manual_task_notification_template'
        taskType = 'MANUAL'
        approver = [
          'admin',
        ]

        // Custom properties
        subservice = ''
      }

      task 'Create ServiceNow ticket', {
        actualParameter = [
          'config_name': '/projects/AcmeGlobal/pluginConfigurations/WesSnowTest',
          'content': '''{
"description":"Change request created from ElectricFlow Pipeline -  $[/myPipelineRuntime/name]. k8-microblog Application deployed to the PM environment and testing is done. Please approve the Change Request to begin the Production deployment. More details can be found by following the URL in the \'Activity\' field below.",
"comments":"[code] <a href=\'https://sda.preview.cb-demos.io/flow/?s=Flow+Tools&ss=Flow#pipeline-run/$[/myPipeline/id]/$[/myPipelineRuntime/id]\'> Link to the ElectricFlow Pipeline </a> <br> <p> <b>Release Evidence:</b>$[/javascript
var props = ""; 
for (var propName in myPipelineRuntime.stages[\'Release Readiness\'].ec_summary.properties) {
    if (propName !== "Jenkins Report:") 
      var props = props +"<br>" + propName + " = " + myPipelineRuntime.stages[\'Release Readiness\'].ec_summary[propName].value;
} 
props.replace(/"/g, \'\\\'\'); 
][/code]"
}''',
          'correlation_display': 'ElectricFlow Pipeline Gate',
          'correlation_id': '/flowRuntime/$[/myPipelineRuntime/id]/stage/PROD/gate/PRE/taskName/WaitforCR_Approval',
          'property_sheet': '/myStageRuntime',
          'short_description': 'Change Request - K8 App',
        ]
        errorHandling = 'continueOnError'
        subpluginKey = 'EC-ServiceNow'
        subprocedure = 'CreateChangeRequest'
        taskType = 'PLUGIN'

        // Custom properties
        subservice = ''
      }
    }

    stage 'Prod', {
      colorCode = '#d62728'
      duration = '2880'
      pipelineName = 'Template Release Workflow'
      plannedEndDate = '2021-08-21T00:00'
      plannedStartDate = '2021-08-19T00:00'
      gate 'PRE', {
        task 'WaitforCR_Approval', {
          gateType = 'PRE'
          notificationEnabled = '1'
          notificationTemplate = 'ec_default_gate_task_notification_template'
          taskType = 'APPROVAL'
          approver = [
            'sa-users',
            'ServiceNowChangeApprovers',
          ]
        }
      }

      task 'Wait for CR is Scheduled', {
        actualParameter = [
          'Configuration': '/projects/POC/pluginConfigurations/snow',
          'PollingInterval': '30',
          'RecordID': '$[/myPipelineRuntime/stages/QA/ChangeRequestNumber]',
          'TargetState': '-2',
        ]
        enabled = '0'
        subprocedure = 'Poll for target CR state'
        subproject = 'ServiceNow'
        taskType = 'PROCEDURE'
      }

      task 'Implement Change Request Ticket', {
        actualParameter = [
          'config_name': '/projects/AcmeGlobal/pluginConfigurations/WesSnowTest',
          'content': '{"state":"-1"}',
          'filter': '',
          'property_sheet': '/myJobStep',
          'record_id': 'change_request.$[/myPipelineRuntime/stages/QA/ChangeRequestSysID]',
        ]
        subpluginKey = 'EC-ServiceNow'
        subprocedure = 'UpdateRecord'
        taskType = 'PLUGIN'
      }

      task 'Deploy to Prod', {
        deployerRunType = 'serial'
        enabled = '0'
        taskType = 'DEPLOYER'

        // Custom properties
        subservice = ''
      }

      task 'Email: Deployment Started to PROD', {
        actualParameter = [
          'advancedOption': '',
          'attachmentList': '',
          'bccList': '',
          'ccList': '',
          'configName': 'default',
          'headerList': '',
          'html': '''<!doctype html>
<html>
  <head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>CloudBees Software Delivery Platform</title>
    <style>
@media only screen and (max-width: 620px) {
  table.body h1 {
    font-size: 28px !important;
    margin-bottom: 10px !important;
  }

  table.body p,
table.body ul,
table.body ol,
table.body td,
table.body span,
table.body a {
    font-size: 16px !important;
  }

  table.body .wrapper,
table.body .article {
    padding: 10px !important;
  }

  table.body .content {
    padding: 0 !important;
  }

  table.body .container {
    padding: 0 !important;
    width: 100% !important;
  }

  table.body .main {
    border-left-width: 0 !important;
    border-radius: 0 !important;
    border-right-width: 0 !important;
  }

  table.body .btn table {
    width: 100% !important;
  }

  table.body .btn a {
    width: 100% !important;
  }

  table.body .img-responsive {
    height: auto !important;
    max-width: 100% !important;
    width: auto !important;
  }
}
@media all {
  .ExternalClass {
    width: 100%;
  }

  .ExternalClass,
.ExternalClass p,
.ExternalClass span,
.ExternalClass font,
.ExternalClass td,
.ExternalClass div {
    line-height: 100%;
  }

  .apple-link a {
    color: inherit !important;
    font-family: inherit !important;
    font-size: inherit !important;
    font-weight: inherit !important;
    line-height: inherit !important;
    text-decoration: none !important;
  }

  #MessageViewBody a {
    color: inherit;
    text-decoration: none;
    font-size: inherit;
    font-family: inherit;
    font-weight: inherit;
    line-height: inherit;
  }

  .btn-primary table td:hover {
    background-color: #34495e !important;
  }

  .btn-primary a:hover {
    background-color: #34495e !important;
    border-color: #34495e !important;
  }
}
</style>
  </head>
  <body class="" style="background-color: #f6f6f6; font-family: sans-serif; -webkit-font-smoothing: antialiased; font-size: 14px; line-height: 1.4; margin: 0; padding: 0; -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;">
    <span class="preheader" style="color: transparent; display: none; height: 0; max-height: 0; max-width: 0; opacity: 0; overflow: hidden; mso-hide: all; visibility: hidden; width: 0;">This is preheader text. Some clients will show this text as a preview.</span>
    <table role="presentation" border="0" cellpadding="0" cellspacing="0" class="body" style="border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f6f6f6; width: 100%;" width="100%" bgcolor="#f6f6f6">
      <tr>
        <td style="font-family: sans-serif; font-size: 14px; vertical-align: top;" valign="top">&nbsp;</td>
        <td class="container" style="font-family: sans-serif; font-size: 14px; vertical-align: top; display: block; max-width: 580px; padding: 10px; width: 580px; margin: 0 auto;" width="580" valign="top">
          <div class="content" style="box-sizing: border-box; display: block; margin: 0 auto; max-width: 580px; padding: 10px;">

            <!-- START CENTERED WHITE CONTAINER -->
            <table role="presentation" class="main" style="border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; background: #ffffff; border-radius: 3px; width: 100%;" width="100%">

              <!-- START MAIN CONTENT AREA -->
              <tr>
                <td class="wrapper" style="font-family: sans-serif; font-size: 14px; vertical-align: top; box-sizing: border-box; padding: 20px;" valign="top">
                  <table role="presentation" border="0" cellpadding="0" cellspacing="0" style="border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: 100%;" width="100%">
                    <tr>
                      <td style="font-family: sans-serif; font-size: 14px; vertical-align: top;" valign="top">
                        <p style="font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;">Hi $[/myUser/name],</p>
                        <p style="font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;">Deployment Pipeline: $[/myPipeline/name] was initiated on $[/timestamp yyyy-MMM-d hh:mm]</p>
                        <table role="presentation" border="0" cellpadding="0" cellspacing="0" class="btn btn-primary" style="border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; box-sizing: border-box; width: 100%;" width="100%">
                          <tbody>
                            <tr>
                              <td align="left" style="font-family: sans-serif; font-size: 14px; vertical-align: top; padding-bottom: 15px;" valign="top">
                                <table role="presentation" border="0" cellpadding="0" cellspacing="0" style="border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: auto;">
                                  <tbody>
                                    <tr>
                                      <td style="font-family: sans-serif; font-size: 14px; vertical-align: top; border-radius: 5px; text-align: center; background-color: #3498db;" valign="top" align="center" bgcolor="#3498db"> <a href="http://$[/server/webServerHost]/flow/#pipeline-run/$[/myPipeline/id]/$[/myPipelineRuntime/flowRuntimeId]" target="_blank" style="border: solid 1px #3498db; border-radius: 5px; box-sizing: border-box; cursor: pointer; display: inline-block; font-size: 14px; font-weight: bold; margin: 0; padding: 12px 25px; text-decoration: none; text-transform: capitalize; background-color: #3498db; border-color: #3498db; color: #ffffff;">View Pipeline Run</a> </td>
                                    </tr>
                                  </tbody>
                                </table>
                              </td>
                            </tr>
                          </tbody>
                        </table>
                        <p style="font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;">This email was generated automatically. </p>
                        <p style="font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;">Have a nice day!</p>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>

            <!-- END MAIN CONTENT AREA -->
            </table>
            <!-- END CENTERED WHITE CONTAINER -->

            <!-- START FOOTER -->
            <div class="footer" style="clear: both; margin-top: 10px; text-align: center; width: 100%;">
              <table role="presentation" border="0" cellpadding="0" cellspacing="0" style="border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: 100%;" width="100%">
                <tr>
                  <td class="content-block" style="font-family: sans-serif; vertical-align: top; padding-bottom: 10px; padding-top: 10px; color: #999999; font-size: 12px; text-align: center;" valign="top" align="center">
                    <span class="apple-link" style="color: #999999; font-size: 12px; text-align: center;">Company Inc, 3 Abbey Road, San Francisco CA 94102</span>
                  </td>
                </tr>
                <tr>
                  <td class="content-block powered-by" style="font-family: sans-serif; vertical-align: top; padding-bottom: 10px; padding-top: 10px; color: #999999; font-size: 12px; text-align: center;" valign="top" align="center">
                    Powered by <a href="https://www.cloudbees.com" style="color: #999999; font-size: 12px; text-align: center; text-decoration: none;">CloudBees</a>.
                  </td>
                </tr>
              </table>
            </div>
            <!-- END FOOTER -->

          </div>
        </td>
        <td style="font-family: sans-serif; font-size: 14px; vertical-align: top;" valign="top">&nbsp;</td>
      </tr>
    </table>
  </body>
</html>''',
          'htmlFile': '',
          'htmlType': 'text',
          'inlineList': '',
          'message': 'html',
          'multipartMode': 'default',
          'raw': '',
          'rawFile': '',
          'rawType': 'text',
          'subject': 'Deployment to $[/myStage/name]',
          'text': '',
          'textFile': '',
          'textType': 'text',
          'toList': 'snaqvi@cloudbees.com',
        ]
        resourceName = 'k8s-agent'
        subpluginKey = 'EC-SendEmail'
        subprocedure = 'Send Email'
        taskType = 'PLUGIN'
      }

      task 'Run smoke tests', {
        command = '''echo "Run smoke tests"
  ectool setProperty "/myStageRuntime/ec_summary/Link to Application" "<html><a href="http://35.197.99.24/jpetstore" target=_blank>Click</a></html>"'''
        taskType = 'COMMAND'

        // Custom properties
        subservice = ''
      }

      task 'Review Change Request Ticket', {
        actualParameter = [
          'config_name': '/projects/AcmeGlobal/pluginConfigurations/WesSnowTest',
          'content': '{"state":"0"}',
          'filter': '',
          'property_sheet': '/myJobStep',
          'record_id': 'change_request.$[/myPipelineRuntime/stages/QA/ChangeRequestSysID]',
        ]
        subpluginKey = 'EC-ServiceNow'
        subprocedure = 'UpdateRecord'
        taskType = 'PLUGIN'
      }

      task 'Close Change Request Ticket', {
        actualParameter = [
          'config_name': '/projects/AcmeGlobal/pluginConfigurations/WesSnowTest',
          'content': '{"state":"3","close_notes":"\\"Closed by CDRO\\""}',
          'filter': '',
          'property_sheet': '/myJobStep',
          'record_id': 'change_request.$[/myPipelineRuntime/stages/QA/ChangeRequestSysID]',
        ]
        subpluginKey = 'EC-ServiceNow'
        subprocedure = 'UpdateRecord'
        taskType = 'PLUGIN'
      }
    }

    // Custom properties

    property 'ec_counters', {

      // Custom properties
      pipelineCounter = '14'
    }
  }
}
