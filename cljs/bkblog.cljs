(ns bkblog.core
  (:require [enfocus.core :as ef]
            [enfocus.effects :as effects]
            [enfocus.events :as ev]
            [bkblog.async :as bkblogasync :refer [aslisten aslisten-live]]
            [cljs.core.async :refer [put! chan <!]]
            [clojure.string :as string :refer [join split replace]])
  (:require-macros [enfocus.macros :as em]
                   [cljs.core.async.macros :refer [go]]))

(def log #(.log js/console %))

(def root-path (replace (replace js/window.location.href (str "http://" js/window.location.host) "") js/window.location.hash ""))

(def viewer (str "viewer/web/viewer.html?zoom=auto,0,798&file=" root-path "posts/"))

(em/deftemplate article-frame :compiled "cljs/templates/frame.html" [pdf-path]
  ["iframe"] (ef/set-attr :src (str pdf-path ".pdf")))

(em/deftemplate article-list "cljs/templates/navigation.html" [])

(defn set-window-hash-args [args-map]
  (let [hash-str (reduce (fn [old-str [k v]] (str old-str (name k) "=" v "&")) "#" args-map)
        clean-hash-str (subs hash-str 0 (dec (count hash-str)))]
    (set! js/window.location.hash clean-hash-str)))

(defn set-hash [pdf]
	(set-window-hash-args {:post pdf}))

(defn set-height []
  (let [window-height (- (.-clientHeight (.-documentElement js/document)) 45)]
  	(ef/at ".flex, .blog-article, .wrapper" (ef/set-attr :style (str "height:" window-height "px;")))))

(defn selected-article [pdf]
  (ef/at "[data-article]" (ef/remove-class "highlight"))
  (ef/at (str "[data-pdf=" pdf "]") 
          (ef/add-class "highlight")))

(defn set-mobile-menu []
  (ef/at ".mobile-menu" (ev/listen :click 
    #((let [current-class (keyword (ef/from ".left" (ef/get-attr :rel)))]
      (log current-class)
      (if (= current-class :visible)
        (ef/at ".left" (ef/set-attr :rel "hidden"))
        (ef/at ".left" (ef/set-attr :rel "visible"))))))))

(em/defaction toggle-pdf [pdf]
	[".blog-article"] (ef/set-attr :src (str viewer pdf ".pdf")))

(let [menu-clicks (aslisten-live "li[data-article]" :click)]
  (go (while true
  	(let [pdf (ef/from (<! menu-clicks) (ef/get-attr :data-pdf))]
        (if (= pdf (subs js/window.location.hash 6))
          (log "No change")
          (do 
            (toggle-pdf pdf)
            (selected-article pdf)
            (set-hash pdf)))))))

(defn init-pdf []
  (let [latest-pdf (ef/from "li[data-article]:first-child" (ef/get-attr :data-pdf))
        pdf-file (if (empty? (str js/window.location.hash)) latest-pdf (subs js/window.location.hash 6))
        pdf-path (str viewer pdf-file)]
        (set-hash pdf-file)
        (selected-article pdf-file)
        (ef/at ".right" 
          (ef/content (article-frame pdf-path)))))

(defn setup []
  (ef/at ".blog-navigation" (ef/html-content (article-list)))
  (set-mobile-menu)
  (init-pdf)
  (set-height))

(em/wait-for-load (setup))