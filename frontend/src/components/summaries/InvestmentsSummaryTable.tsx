import {
  Button,
  Card,
  CardBody,
  CardHeader,
  Icon,
} from "@trussworks/react-uswds";
import { useState } from "react";
import GoalIndividualCard from "./GoalIndividualCard";
import React from "react";
import { useGetGoalsByEmailQuery } from "../../app/api/goalsApi";
import { useSelector } from "react-redux";
import { selectUserEmail } from "../../app/features/userSlice";
import { Link, Navigate } from "react-router-dom";
import "../../styles/goalCard.css";
import IndividualMetricCard from "../investments/InvestmentMetricCard";
import { useGetInvestmentsByEmailQuery } from "../../app/api/investmentsApi";

export default function InvestmentsSummaryTable() {
  const userEmail = useSelector(selectUserEmail);

  return (
    <>
      <Card
        headerFirst
        //gridLayout={{ tablet: { col: 6 }, desktop: { col: "fill" } }}
        className="goalSummary"
      >
        <CardHeader className="bg-lightest text-left">
          <h2>Investments</h2>
        </CardHeader>
        <CardBody className="padding-top-3">
          <IndividualMetricCard />
          <Link style={{ textDecoration: "none" }} to={"/investments"}>
            <Button
              type={"button"}
              className="bg-mint"
              style={{
                padding: 15,
                borderRadius: "10px",
                display: "flex",
                margin: "auto",
              }}
            >
              Click here to get started on your investments!
            </Button>
          </Link>
        </CardBody>
        {/* <CardFooter>
          if needed footer goes here
        </CardFooter> */}
      </Card>
    </>
  );
}
