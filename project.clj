(defproject anathema-online "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.217" :scope "provided"]
                 [com.cognitect/transit-clj "0.8.300"]
                 [com.cognitect/transit-cljs "0.8.243"]
                 [ring "1.6.2"]
                 [ring/ring-defaults "0.3.1"]
                 [bk/ring-gzip "0.2.1"]
                 [radicalzephyr/ring.middleware.logger "0.6.0"]
                 [compojure "1.6.0"]
                 [environ "1.1.0"]
                 [enlive "1.1.6"]
                 [com.stuartsierra/component "0.3.2"]
                 [org.danielsz/system "0.4.0"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [rum "0.11.2"]
                 [lambdaisland/garden-watcher "0.3.1"]
                 [com.rpl/specter "1.1.0"]
                 [com.novemberain/monger "3.1.0"]
                 [com.cemerick/url "0.1.1"]
                 [gzip-util "0.1.0-SNAPSHOT"]
                 [com.cemerick/url "0.1.1"]
                 [byte-transforms "0.1.4"]
                 [jstrutz/hashids "1.0.1"]
                 [org.clojure/test.check "0.10.0-alpha2"]
                 [org.clojure/core.async  "0.4.474"]]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-environ "1.1.0"]]

  :min-lein-version "2.6.1"

  :source-paths ["src/clj" "src/cljs" "src/cljc" "src/cljservice"]

  :test-paths ["test/clj" "test/cljc"]

  :clean-targets ^{:protect false} [:target-path :compile-path "resources/public/js"]

  :uberjar-name "anathema-online.jar"

  ;; Use `lein run` if you just want to start a HTTP server, without figwheel
  :main anathema-online.application

  ;; nREPL by default starts in the :main namespace, we want to start in `user`
  ;; because that's where our development helper functions like (go) and
  ;; (browser-repl) live.
  :repl-options {:init-ns user}

  :cljsbuild {:builds
              [{:id "app"
                :source-paths ["src/cljs" "src/cljc" "dev"]

                :figwheel {:on-jsload "anathema-online.system/reset"}

                :compiler {:main cljs.user
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/anathema_online.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true}}

               {:id "test"
                :source-paths ["src/cljs" "test/cljs" "src/cljc" "test/cljc"]
                :compiler {:output-to "resources/public/js/compiled/testable.js"
                           :main anathema-online.test-runner
                           :optimizations :advanced}}

               {:id "worker"
                :source-paths ["src/cljservice"]
                :compiler {:output-to "resources/public/js/compiled/service_worker.js"
                           :output-dir "resources/public/js/compiled/out/worker"
                           :optimizations :advanced}}

               {:id "worker-dev"
                :source-paths ["src/cljservice"]
                :compiler {:output-to "resources/public/js/compiled/service_worker.js"
                           :output-dir "resources/public/js/compiled/out/worker-dev"
                           :optimizations :none}}

               {:id "min"
                :source-paths ["src/cljs" "src/cljc"]
                :jar true
                :compiler {:main anathema-online.system
                           :output-to "resources/public/js/compiled/anathema_online.js"
                           :output-dir "target"
                           :source-map-timestamp true
                           :optimizations :advanced
                           :pretty-print false}}]}

  ;; When running figwheel from nREPL, figwheel will read this configuration
  ;; stanza, but it will read it without passing through leiningen's profile
  ;; merging. So don't put a :figwheel section under the :dev profile, it will
  ;; not be picked up, instead configure figwheel here on the top level.

  :figwheel {;; :http-server-root "public"       ;; serve static assets from resources/public/
             ;; :server-port 3449                ;; default
             ;; :server-ip "127.0.0.1"           ;; default
             :css-dirs ["resources/public/css"]  ;; watch and update CSS

             ;; Start an nREPL server into the running figwheel process. We
             ;; don't do this, instead we do the opposite, running figwheel from
             ;; an nREPL process, see
             ;; https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl
             ;; :nrepl-port 7888

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             :server-logfile "log/figwheel.log"}

  :doo {:build "test"}

  :profiles {:dev
             {:dependencies [[figwheel "0.5.16-SNAPSHOT"]
                             [figwheel-sidecar "0.5.11"]
                             [com.cemerick/piggieback "0.2.2"]
                             [org.clojure/tools.nrepl "0.2.13"]
                             [lein-doo "0.1.7"]
                             [reloaded.repl "0.2.3"]]
              :plugins [[lein-figwheel "0.5.16-SNAPSHOT"]
                        [lein-doo "0.1.7"]]
              :env {:mongodb-uri "mongodb%3A%2F%2Flocalhost%3A27017%2Fanathema"
                    :masterkey "devkey42"}
              :source-paths ["dev" "src/cljservice"]
              :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}

             :uberjar
             {:source-paths ^:replace ["src/clj" "src/cljc" "src/cljservice"]
              :prep-tasks ["compile"
                           ["cljsbuild" "once" "min"]
                           ["cljsbuild" "once" "worker"]
                           ["run" "-m" "garden-watcher.main" "anathema-online.styles"]]
              :hooks []
              :omit-source true
              :aot :all}})
