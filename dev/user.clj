(ns user
  (:require [anathema-online.application]
            [com.stuartsierra.component :as component]
            [figwheel-sidecar.config :as fw-config]
            [figwheel-sidecar.system :as fw-sys]
            [clojure.tools.namespace.repl :refer [set-refresh-dirs]]
            [reloaded.repl :refer [system init]]
            [ring.middleware.reload :refer [wrap-reload]]
            [figwheel-sidecar.repl-api :as figwheel]
            [garden-watcher.core :refer [new-garden-watcher]]
            [anathema-online.config :refer [config]]
            [anathema-online.components.mongo :as db]
            [com.rpl.specter :as sp]))

(defn print-pass [n] (println (str "printed and passed- " n) n))

(defn dev-system []
  (assoc (anathema-online.application/app-system (config))
    :figwheel-system (fw-sys/figwheel-system (fw-config/fetch-config))
    :css-watcher (fw-sys/css-watcher {:watch-paths ["resources/public/css"]})))
    ;:garden-watcher (new-garden-watcher ['anathema-online.garden.core])))

(set-refresh-dirs "src" "dev")
(reloaded.repl/set-init! #(dev-system))

(defn cljs-repl []
  (fw-sys/cljs-repl (:figwheel-system system)))

;; Set up aliases so they don't accidentally
;; get scrubbed from the namespace declaration
(def start reloaded.repl/start)
(def stop reloaded.repl/stop)
(def go reloaded.repl/go)
(def reset reloaded.repl/reset)
(def reset-all reloaded.repl/reset-all)

;; deprecated, to be removed in Chestnut 1.0
(defn run []
  (println "(run) is deprecated, use (go)")
  (go))

(defn browser-repl []
  (println "(browser-repl) is deprecated, use (cljs-repl)")
  (cljs-repl))

(defn backup-dev-db []
  (db/backup-dev-db! (:db (:db reloaded.repl/system))))

(defn restore-dev-db []
  (db/restore-dev-db! (:db (:db reloaded.repl/system))))