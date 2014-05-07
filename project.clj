(defproject bkblog "1"
  :description "UI for boriskourt.com"
  :url "http://boriskourt.com/"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2202"]
                 [com.cemerick/url "0.1.1"]
                 [kioo "0.4.1-SNAPSHOT"]
                 [om "0.6.2"]
                 [org.clojure/core.async "0.1.278.0-76b25b-alpha"]
                 [secretary "1.1.0"]]
  :plugins [[lein-cljsbuild "1.0.0"]]
  :cljsbuild {
              :builds [{:source-paths ["cljs"]
                        :compiler {:output-to "assets/js/core.js"
                                   :output-dir "target"
                                   :optimizations :advanced
                                   :pretty-print false
                                   ;;:source-map "core.js.map"
                                   }}]})