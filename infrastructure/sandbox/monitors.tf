module "telemetry" {
  source  = "app.terraform.io/spotify/telemetry/datadog"
  version = "0.43.0"

  environment     = "sandbox"
  pager_duty      = ""
  datadog_on_call = ""
  services        = var.services
  service_slos    = var.service_slos
  slack_channel   = "@slack-capybaras-team-monitor-alerts-sandbox"
  team            = "capybaras"

  datadog_api_key = var.DATADOG_API_KEY
  datadog_app_key = var.DATADOG_APP_KEY

  service_endpoints = {}
}
