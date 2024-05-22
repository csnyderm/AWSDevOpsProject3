import { CardGroup } from "@trussworks/react-uswds";
import { useSelector } from "react-redux";

import { selectUserEmail } from "../app/features/userSlice";
import { useTranslation } from "react-i18next";
import SliceTable from "../components/investments/SliceTable";
import InvestmentMetricCard from "../components/investments/InvestmentMetricCard";
import AddInvestmentCard from "../components/investments/AddInvestmentCard";
import { useEffect, useRef, useState } from "react";
import { useGetInvestmentsByEmailQuery } from "../app/api/investmentsApi";

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

export default function Investments() {
  const userEmail = useSelector(selectUserEmail);
  const { data, error, isSuccess, isLoading } =
    useGetInvestmentsByEmailQuery(userEmail);
  console.log("The data is: " + data);
  const { t } = useTranslation();

  {
    /** Array of Investments for displaying via the IndividualSlice.tsx card components  */
  }
  const investmentsArray: {
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
  }[] = [];

  const [array, setArray] = useState<tempInvestmentData[]>(investmentsArray);
  console.log("The array is: " + array);
  const [s, setS] = useState<string>();

  useEffect(() => {
    const fetchData = async () => {
      if (isSuccess) {
        for (const investment of data!) {
          console.log("ID: " + investment.id);
          const key = "C1DME2TPFODPJ0MK";
          var url =
            "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" +
            investment.symbol +
            "&apikey=" +
            key;
          await fetch(url)
            .then((response) => {
              if (response.status !== 200) {
                console.log(
                  "Looks like there was a problem. Status Code: " +
                    response.status
                );
                return;
              }
              return response.json();
            })
            .then((myJson) => {
              console.log(myJson);
              setS(investment.symbol);
              const Entry = {
                id: String(investment.id),
                email: String(userEmail),
                symbol: String(investment.symbol),
                name: String(investment.stockName),
                quantity: Number(investment.shares),
                purchasePrice: Number(investment.purchasePrice),
                price: Number(myJson["Global Quote"]["05. price"]),
                priceChange: Number(myJson["Global Quote"]["09. change"]),
                marketValue: (
                  Number(myJson["Global Quote"]["05. price"]) *
                  Number(investment.shares)
                ).toFixed(2),
                costBasis: (
                  Number(investment.shares) * Number(investment.purchasePrice)
                ).toFixed(2),
                gainLoss: (
                  Number(myJson["Global Quote"]["05. price"]) *
                    Number(investment.shares) -
                  Number(investment.shares) * Number(investment.purchasePrice)
                ).toFixed(2),
              };
              investmentsArray.push(Entry);
              console.log(investmentsArray);
              setArray(investmentsArray);
              return;
            });
        }
      }
    };
    fetchData();
  }, [data]);

  return (
    <>
      <h1 className="text-center padding-bottom-5">
        {" "}
        {t("Investments.My Portfolio")}{" "}
      </h1>
      <div
        className="grid-row margin-x-205 margin-bottom-7 flex-justify-center"
        data-testid="grid"
      >
        <div
          className="grid-col-3 padding-x-205 margin-bottom-7"
          data-testid="grid"
        >
          {/** Edit/Update + Pie Chart Card */}
          <CardGroup className="flex-justify-center">
            <AddInvestmentCard />
          </CardGroup>
        </div>
        <div className="grid-col-9 padding-x-205" data-testid="grid">
          {/** Summary/Metrics Tab  */}
          <CardGroup className="flex-justify-center">
            <InvestmentMetricCard />
          </CardGroup>
          {/** Table of added stocks */}
          <CardGroup className="flex-justify-center">
            <SliceTable tempInvestmentData={array} symbol={s!} />
          </CardGroup>
        </div>
      </div>
    </>
  );
}
