import React, { useState } from 'react'
import  "../styles/ProgressBar.css";
import { useTranslation } from 'react-i18next';

interface GoalCardProps {
  name: string;
  goalAmount: number;
  amountSaved: number;
  progress : number;
}
export default function ProgressBar(props: GoalCardProps) {


  const {t} = useTranslation();


 
  

  return (
    <>
      <section className="progress-container">
        <div>
          <h3>{t("Goals.Progress")} : <span className='text-mint'>{props.progress}%</span></h3>
        </div>
        <progress className="progress" max="100" value ={props.progress}> </progress>
      </section>
    </>
  )
}
