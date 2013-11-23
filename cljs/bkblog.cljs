(ns bkblog.core
  (:require [enfocus.core :as ef]
            [enfocus.effects :as effects]
            [enfocus.events :as ev]
            [bkblog.async :as a :refer [aslisten]]
            [cljs.core.async :refer [put! chan <!]]
            [clojure.string :as string :refer [join split]])
  (:require-macros [enfocus.macros :as em]
                   [cljs.core.async.macros :refer [go]]))

(def log #(.log js/console %))

(def viewer "viewer/web/viewer.html?zoom=auto,0,798&file=/sb/pdfjs/web/posts/")

(em/deftemplate article-frame :compiled "templates/frame.html" [pdf-path]
  ["iframe"] (ef/set-attr :src pdf-path))

(defn set-window-hash-args [args-map]
  (let [hash-str (reduce (fn [old-str [k v]] (str old-str (name k) "=" v "&")) "#" args-map)
        clean-hash-str (subs hash-str 0 (dec (count hash-str)))]
    (set! js/window.location.hash clean-hash-str)))

(defn set-hash [pdf]
	(set-window-hash-args {:file pdf}))

(defn set-height []
  (let [window-height (- (.-clientHeight (.-documentElement js/document)) 45)]
  	(ef/at ".flex, .blog-article" (ef/set-attr :height window-height))))

(em/defaction toggle-pdf [pdf]
	[".blog-article"] (ef/set-attr :src (str viewer pdf)))

(let [menu-clicks (aslisten "li[data-article]" :click)]
  (go (while true
  	(let [pdf (ef/from (<! menu-clicks) (ef/get-attr :data-pdf))]
        (if (= pdf (subs js/window.location.hash 6))
          (log "No change")
          (do 
            (toggle-pdf pdf)
            (set-hash pdf)))))))

(defn init-pdf []
  (let [latest-pdf (ef/from "li[data-article]:first-child" (ef/get-attr :data-pdf))
        pdf-file (if (empty? (str js/window.location.hash)) latest-pdf (subs js/window.location.hash 6))
        pdf-path (str viewer pdf-file)]
        (set-hash pdf-file)
        (ef/at ".right" 
          (ef/content (article-frame pdf-path)))))

(defn setup []
  (init-pdf)
  (set-height))

(setup)