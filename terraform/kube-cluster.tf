resource "google_container_cluster" "tf-tests" {
    name = "tf-test"
    location = "${data.google_compute_zones.available.names[0]}"
    initial_node_count = 1

    node_version = "1.14.10-gke.27"
    min_master_version = "1.14.10-gke.27"

    node_locations = [
        "${data.google_compute_zones.available.names[1]}",
        "${data.google_compute_zones.available.names[2]}"
    ]

    master_auth {
        username = ""
        password = ""
    }

    node_config {
        oauth_scopes = [
            "https://www.googleapis.com/auth/compute",
            "https://www.googleapis.com/auth/devstorage.read_only",
            "https://www.googleapis.com/auth/logging.write",
            "https://www.googleapis.com/auth/monitoring",
        ]
    }
}