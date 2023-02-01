pluginConfiguration 'jira', {
  field = [
    'auth': 'basic',
    'credential': 'credential',
    'debugLevel': '0',
    'ignoreSSLErrors': '0',
    'url': 'https://cbpoc.atlassian.net',
  ]
  pluginKey = 'EC-JIRA'
  projectName = 'POC'

  addCredential 'credential', {
    passwordRecoveryAllowed = '1'
    userName = 'snaqvi@cloudbees.com'
  }
}