(ns bkblog.dm
  (:require [enfocus.core :as ef]
            [enfocus.effects :as effects]
            [enfocus.events :as ev]
            [bkblog.async :as bkblogasync :refer [aslisten aslisten-live]]
            [clojure.string :as string :refer [join split replace]]
            [cljs.core.async :refer [put! chan <!]])
  (:require-macros [enfocus.macros :as em]
                   [cljs.core.async.macros :refer [go]]))

(def log #(.log js/console %))

(em/defsnippet core-menu-item :compiled "cljs/templates/dm.html" [".fm--core li"] 
	[context]
	[".fm__link"] (do 
					(ef/set-attr :data-context context)
					(ef/html-content (str context "<span class='fm__link__arrow'>7</span>"))))

(em/defsnippet sub-menu-item :compiled "cljs/templates/dm.html" [".fm__subnav li"] 
	[a]
	[".fm__link"] (do 
					(ef/set-attr :href a)
					(ef/content a)))
; (comment
;   Keys should be:
;   Logo - >  map name link
;   Main - >  map name type
;   Sub  - >  map name type link
;   Social -> map name link
;   Info - >  string description

;   contains?
;   select-keys map keyseq
; {:logo   {	:title "BorisKourt" :url "/" } 
; :main   {	"Projects" {"A project" "#"
; "A cat project" "#"} 
; "Talking"  {"A rat talk" "#"
; "Wombats" "#"}
; "Other"    {"Rhombus" "#"}}
; :social {}
; :info 	 {}}
;   )

(em/deftemplate menu :compiled "cljs/templates/dm.html" [data]
	(let [logo (select-keys data [:logo])]
		(if (contains? :url logo)
			[".fm--logo .fm__link"] (ef/content "hat")
			[".fm--logo .fm__link"] (ef/set-attr :href "/")))
	;[".fm--logo .fm__link"] (ef/content nom) 
	;[".fm--logo .fm__link"] (ef/set-attr :href home)
	;[".fm--core ul"] (ef/html-content (map #(core-menu-item (core-links %)) (keys core-links)))
	;[".fm__subnav ul"] (ef/html-content (map #(sub-menu-item (sub-links %)) (keys sub-links)))
	)

