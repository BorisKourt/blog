(ns bkblog.core
  (:require [enfocus.core :as ef]
            [enfocus.effects :as effects]
            [enfocus.events :as ev]
            [bkblog.async :as bkblogasync :refer [aslisten aslisten-live]]
            [bkblog.dm :as dm]
            [cljs.core.async :refer [put! chan <!]]
            [clojure.string :as string :refer [join split replace]])
  (:require-macros [enfocus.macros :as em]
                   [cljs.core.async.macros :refer [go]]))

(def log #(.log js/console %))

(defn in? 
  "true if seq contains elm"
  [seq elm]  
  (some #(= elm %) seq))

(def root-path (replace (replace js/window.location.href (str "http://" js/window.location.host) "") js/window.location.hash ""))

(def viewer (str "viewer/web/viewer.html?zoom=pagewidth&file=" root-path "posts/"))

(em/deftemplate article-frame :compiled "cljs/templates/frame.html" [pdf-path]
  ["iframe"] (ef/set-attr :src (str pdf-path ".pdf")))

(em/deftemplate article-list :compiled "cljs/templates/navigation.html" [type]
  ["li"] (if 
    (not= type :standard) 
    (ef/do-> 
      (ef/remove-attr :data-article)
      (ef/set-attr :data-direct ""))))

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
    #((let [current-class (ef/from ".left" (ef/get-attr :rel))]
      (log current-class)
      (if (= current-class "visible")
        (ef/at ".left" (ef/set-attr :rel "hidden"))
        (ef/at ".left" (ef/set-attr :rel "visible"))))))))

(em/defaction toggle-pdf [pdf]
	[".blog-article"] (ef/set-attr :src (str viewer pdf ".pdf")))

(let [menu-clicks (aslisten-live "li[data-article]" :click)]
  (go (while true
  	(let [pdf (ef/from (<! menu-clicks) (ef/get-attr :data-pdf))]
        (if (not= pdf (subs js/window.location.hash 6))
          (do 
            (toggle-pdf pdf)
            (selected-article pdf)
            (set-hash pdf)))))))

; (let [direct-menu-clicks (aslisten-live "li[data-direct]" :mouseover)]
;   (go (while true
;     (let [target (<! direct-menu-clicks)
;           pdf (ef/from target (ef/get-attr :data-pdf))
;           content (ef/from target (ef/get-text))]
;         (ef/at target 
;           (ef/do->
;             (ef/html-content 
;               (str "<a href='" root-path "posts/" pdf ".pdf' target='_blank'>" content "</a>"))))))))

; (defn adjust-alt-menu []
;   (let [ pdf (ef/from target (ef/get-attr :data-pdf))
;          content (ef/from target (ef/get-text))]
;         (ef/at target 
;           (ef/do->
;             (ef/html-content 
;               (str "<a href='" root-path "posts/" pdf ".pdf' target='_blank'>" content "</a>"))
;             (ef/html-content 
;               (str "<a href='" root-path "posts/" pdf ".pdf' target='_blank'>" content "</a>"))))))


(defn init-pdf []
  (let [latest-pdf (ef/from "li[data-article]:first-child" (ef/get-attr :data-pdf))
        pdf-file (if (empty? (str js/window.location.hash)) latest-pdf (subs js/window.location.hash 6))
        pdf-path (str viewer pdf-file)]
        (log latest-pdf)
        (set-hash pdf-file)
        (selected-article pdf-file)
        (ef/at ".right" 
          (ef/content (article-frame pdf-path)))))

(defn css-rules [condition-value]
  (case condition-value
    (:standard) (log "All works")
    (:lacks-flex) (log "Lacks Flex")
    (:lacks-reader) (log "Lacks Rreader")))

(defn setup 
  "Available = [canvas,webwokers,flex]"
  [available]
  (case available
    ([true true true]) (do 
      (ef/at ".blog-navigation" (ef/html-content (article-list :standard)))
      (set-mobile-menu)
      (css-rules :standard)
      (init-pdf))
    ([true true nil] [true nil nil]) (do 
      (ef/at ".blog-navigation" (ef/html-content (article-list :standard)))
      (set-mobile-menu)
      (css-rules :lacks-flex)
      (init-pdf))
    (do 
      (ef/at ".unsupported__menu" (ef/html-content (article-list :lacks-reader)))
      (css-rules :lacks-reader)))
  (set-height)
;;(ef/at ".bktran" (ef/html-content (dm/menu pull-menu)))
)

;; (select-keys map keyseq)
(defn modernity []
  (let [available (split (ef/from "html" (ef/get-attr :class)) " ")
        canvas    (in? available "canvas")
        webwork   (in? available "webworkers")
        flex      (if (in? available "flexbox")
                      true 
                      (if (in? available "flexboxlegacy")
                          true)
                      nil)]
    (em/wait-for-load (setup [canvas webwork flex]))))

(modernity)