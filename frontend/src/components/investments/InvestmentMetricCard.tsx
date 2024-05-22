import { Card, CardHeader, CardBody } from "@trussworks/react-uswds";
import React, { useEffect, useState } from "react";
import {
  Investment,
  useGetInvestmentsByEmailQuery,
} from "../../app/api/investmentsApi";
import { copyWithStructuralSharing } from "@reduxjs/toolkit/dist/query";
import { useSelector } from "react-redux";
import { selectUserEmail } from "../../app/features/userSlice";
import "../../styles/investmentCard.css";
import { useTranslation } from "react-i18next";

interface tempInvestmentData {
  id: string;
  email: string;
  symbol: string;
  name: string;
  quantity: number;
  purchasePrice: number;
  price: number;
  priceChange: number;
  marketValue: string;
  costBasis: string;
  gainLoss: string;
}
export default function IndividualMetricCard() {
  const [t] = useTranslation();
  const userEmail = useSelector(selectUserEmail);

  const { data, error, isSuccess, isLoading } =
    useGetInvestmentsByEmailQuery(userEmail);

  const [num, setNum] = useState(0);
  const [count, setCount] = useState(0);
  const [most, setMost] = useState<Investment>();
  useEffect(() => {
    if (data) {
      let curr = 0;
      let count = 0;
      setMost(data!.at(0));
      data?.forEach((item) => {
        if (Number(item.shares) > Number(most?.shares)) {
          setMost(item);
        }
        console.log("The item is: " + item.symbol);
        curr = curr + item.purchasePrice * item.shares;
        count = count + item.shares;
      });
      setNum(curr);
      setCount(count);
    }
  }, [data]);

  return (
    <Card
      headerFirst
      gridLayout={{ desktop: { col: "fill" } }}
      containerProps={{}}
      className="summaryInvestments"
    >
      <CardHeader className="bg-lightest">
        <h2 className="bold">{t("Investments.Metrics")}</h2>
      </CardHeader>
      <CardBody className="margin-x-5">
        <div style={{ display: "flex", justifyContent: "space-between" }}>
          <div style={{ textAlign: "center", lineHeight: "1.5rem" }}>
            <h2>{t("Investments.Total Invested")}</h2>
            <h1 style={{ fontSize: "1.2rem" }}>
              $ <b className="text-mint">{num.toFixed(2).toString()}</b>
            </h1>
          </div>
          <div style={{ textAlign: "center", lineHeight: "1.5rem" }}>
            <h2>{t("Investments.Total Shares Owned")}</h2>
            <h1 style={{ fontSize: "1.2rem" }}>
              <b className="text-mint">{count.toString()}</b>
            </h1>
          </div>
          <div style={{ textAlign: "center", lineHeight: "1.5rem" }}>
            <h2>{t("Investments.Most Invested Stock")}</h2>
            <h1 style={{ fontSize: "1.2rem", overflowWrap: "break-word" }}>
              <b className="text-mint">{most?.symbol.toString()}</b>
            </h1>
          </div>
        </div>
      </CardBody>
      {/* <CardFooter>
          if needed footer goes here
        </CardFooter> */}
    </Card>
  );
}
