(defproject bkblog "1"
  :description "UI for boriskourt.com"
  :url "http://boriskourt.com/"
  :dependencies [[org.clojure/clojure "1.6.0-alpha2"]
                 [org.clojure/clojurescript "0.0-2080"]
                 [com.cemerick/url "0.1.0"]
                 [enfocus "2.0.2"]
                 [org.clojure/core.async "0.1.262.0-151b23-alpha"]
                 [net.clojure/monads "1.0.0"]]
  :plugins [[lein-cljsbuild "1.0.0"]]
  :cljsbuild {
              :builds [{:source-paths ["cljs"]
                        :compiler {:output-to "assets/js/core.js"
                                   :output-dir "target"
                                   :optimizations :advanced
                                   :pretty-print false
                                   ;;:source-map "core.js.map"
                                   }}]})