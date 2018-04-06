(ns anathema-online.route-def
  (:require [bidi.bidi :as bidi]))

(def routemap ["/" {"js/" {true ::js}
                    "service_worker.js" ::service-worker
                    #{"" "index.html"} ::index
                    "fonts/" {true ::fonts}
                    "favicon.png" ::favicon
                    "img/" {true ::img}
                    "manifest.json" ::manifest
                    "css/" {true ::css}
                    "favicon.ico" ::bad-favicon}])
