(ns pear.core
  (:gen-class)
  (:require [clojure.data.json :as json]
            [digest]))

(def chain (atom []))
(def genesis-block {
  :nonce 0
  :hash "GENESIS"
})

(defn add-block 
  "This adds a block to the chain"
  [chain sender message]
  (let [hash (digest/md5 (json/write-str @chain))]
    (let [block {
        :nonce 0
        :message message 
        :sender sender
        :hash hash
      }]
      (swap! chain conj block))
  chain))

(defn validate
  "this validates the chain by checking the last block and
  recursively checking all before it"
  [chain]
  (if (> (count @chain) 1) ;; if this is not the first block
                           ;; check all previous blocks
    (let [new-chain (drop-last @chain)]
      (let [last-block (last @chain)]
        (let [chain-hash (digest/md5 (json/write-str new-chain))]
          (if (= chain-hash (:hash last-block))
            (validate new-chain) false)))))
  (if (= genesis-block
         (first @chain))
    true false)) ;; if it is first, return true
        ;; TODO: add propper validation

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  (swap! chain conj genesis-block)
  (println (json/write-str
    @(add-block chain "jeff" "hello world")))
  (println (validate chain)))