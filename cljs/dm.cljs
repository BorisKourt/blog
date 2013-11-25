(ns bkblog.dm
  (:require [enfocus.core :as ef]
            [enfocus.effects :as effects]
            [enfocus.events :as ev]
            [bkblog.async :as bkblogasync :refer [aslisten aslisten-live]]
            [cljs.core.async :refer [put! chan <!]])
  (:require-macros [enfocus.macros :as em]
                   [cljs.core.async.macros :refer [go]]))

(em/defsnippet core-menu-item "cljs/templates/dm.html" [".fm--core a"] 
	[context]
	["a"] (ef/set-attr :data-context context))

(em/defsnippet sub-menu-item "cljs/templates/dm.html" [".fm__submenu > li:first-child"] 
	[url]
	["li > a"] (ef/set-attr :src url))

(em/deftemplate menu "cljs/templates/dm.html" [nom home core-links sub-links]
	[".fm--logo .fm__link"] (ef/content nom) 
	[".fm--logo .fm__link"] (ef/set-attr :src home)
	[".fm-core"] (ef/html-content
		(map #(core-menu-item (str (core-links %) "<span class='fm__link__arrow'>7</span>")) (keys core-links))))