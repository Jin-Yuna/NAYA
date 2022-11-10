import React, { useEffect, useRef, useState } from "react";
import { useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import { useAppDispatch } from "../app/hooks";
import styles from "./Card.module.css";
import { getCardInfo, selectImageUrl } from "./cardSlice";
import IMG from "../assets/sample_card.svg";
import { Helmet } from "react-helmet-async";
const Card = () => {
  const { userId, sendCardId }: any = useParams();
  const contentRef = useRef<HTMLDivElement | null>(null);
  const imageUrl = useSelector(selectImageUrl);
  const dispatch = useAppDispatch();
  // render
  useEffect(() => {
    dispatch(getCardInfo({ userId, sendCardId }));
    console.log(userId, sendCardId);
  }, [dispatch, sendCardId, userId]);
  useEffect(() => {
    console.log(imageUrl);
  }, [imageUrl]);

  // action
  const [rotateX, setRotateX] = useState(0.0);
  const [rotateY, setRotateY] = useState(0.0);
  const [transition, setTransition] = useState("all .3s ease");
  const [translateZ, setTranslateZ] = useState("0px");
  // mouse
  const mousemoveOn = (e: MouseEvent) => {
    // console.log(e.pageX, e.pageY);
    let xAxis = (window.innerWidth / 2 - e.pageX) / 12;
    let yAxis = (window.innerHeight / 2 - e.pageY) / 12;
    setRotateX(xAxis);
    setRotateY(yAxis);
  };
  const mouseenterOn = (e: MouseEvent) => {
    setTransition("none");
    setTranslateZ("100px");
  };

  const mouseleaveOn = (e: MouseEvent) => {
    setRotateX(0.0);
    setRotateY(0.0);
    setTransition("all .3s ease");
    setTranslateZ("0px");
  };

  //touch
  const touchmoveOn = (ev: TouchEvent) => {
    // console.log(e.pageX, e.pageY);
    let xAxis = (window.innerWidth / 2 - ev.touches[0].pageX) / 12;
    let yAxis = (window.innerHeight / 2 - ev.touches[0].pageY) / 12;
    setRotateX(xAxis);
    setRotateY(yAxis);
  };
  const touchstartOn = (ev: TouchEvent) => {
    setTransition("none");
    setTranslateZ("100px");
  };

  const touchendOn = (ev: TouchEvent) => {
    setRotateX(0.0);
    setRotateY(0.0);
    setTransition("all .3s ease");
    setTranslateZ("0px");
  };

  useEffect(() => {
    if (!contentRef.current) return;
    const content: HTMLDivElement = contentRef.current;
    // mouse
    content.addEventListener("mousemove", mousemoveOn);
    content.addEventListener("mouseenter", mouseenterOn);
    content.addEventListener("mouseleave", mouseleaveOn);
    // touch
    content.addEventListener("touchmove", touchmoveOn);
    content.addEventListener("touchstart", touchstartOn);
    content.addEventListener("touchend", touchendOn);
    return () => {
      // mouse
      content.removeEventListener("mousemove", mousemoveOn);
      content.removeEventListener("mouseenter", mouseenterOn);
      content.addEventListener("mouseleave", mouseleaveOn);
      // touch
      content.removeEventListener("touchmove", touchmoveOn);
      content.removeEventListener("touchstart", touchstartOn);
      content.removeEventListener("touchend", touchendOn);
    };
  });
  return (
    <div className={styles.container}>
      <Helmet>
        <title>{"나야(Naya) - 카드공유"}</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      </Helmet>
      <div
        className={styles.content}
        ref={contentRef}
        style={{
          transform: `rotateY(${rotateY}deg) rotateX(${rotateX}deg)`,
          transition: `${transition}`,
        }}
      >
        <img
          className={styles.cardImage}
          src={imageUrl ? imageUrl : IMG}
          alt=""
          style={{
            transform: `translateZ(${translateZ})`,
            transition: "all .3s ease",
          }}
        />
      </div>
    </div>
  );
};

export default Card;
