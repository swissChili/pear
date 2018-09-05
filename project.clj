(defproject pear "0.1.0-SNAPSHOT"
  :description "p2p blockchain chat"
  :url "http://github.com/swissChili/pear"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [digest "1.4.8"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [ring/ring-core "1.6.3"]
                 [seesaw "1.5.0"]]
  :main ^:skip-aot pear.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
