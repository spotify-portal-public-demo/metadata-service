data "tfe_outputs" "eks_shared" {
  organization = "spotify"
  workspace    = "EKS-${var.env}"
}
