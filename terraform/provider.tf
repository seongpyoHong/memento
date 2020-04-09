provider "google" {
    credentials = "${file("/Users/seongpyo/workspace/Setting/credentials/terraform-gke.json")}"
    project = "sphong-kuber"
    region = "asia-northeast3"
}