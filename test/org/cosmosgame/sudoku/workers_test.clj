(ns org.cosmosgame.sudoku.workers-test
  (:use clojure.test
        org.cosmosgame.sudoku.core
        org.cosmosgame.sudoku.workers
        org.cosmosgame.sudoku.workers-test
        ))

  (def test-queue (queue))
  (def test-result (promise))
  (defn test-pred
    [x result]
    (println "processing: " x)
    (if (= x "done")
      (deliver result "done!")))

(deftest test-offer
  (let [q (queue)
        _ (offer q "test")]
    (is (= ["test"] (into [] q) ))))

(deftest test-offer-all
  (let [q (queue)
        expected ["one" "two" "three"]
        _ (offer-all q expected)]
    (is (= expected (into [] q) ))))

(deftest test-start-workers
  (let [q (queue)
        result (promise)
        workers (start-workers 3 q test-pred result)]
    (is (= 3 (count workers)))
    (is (every? #(not (realized? %)) workers ))
    (offer q "done")
    (Thread/sleep 600)
    (is (every? realized? workers) )))



