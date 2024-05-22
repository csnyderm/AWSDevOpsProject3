import { useTranslation } from "react-i18next";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";
import { useFindOneQuery } from "../app/api/taxApi";
import { Table } from "@trussworks/react-uswds";
import { selectUserEmail } from "../app/features/userSlice";
import { useSelector } from "react-redux";

export default function PieChartTaxOverview() {

  const { t } = useTranslation();

  //const userEmail: string = "testingNewModel@gmail.com"; // temporary
  const userEmail = useSelector(selectUserEmail);
  const { data, isLoading } = useFindOneQuery(userEmail);


  //const userEmail: string = "testingNewModel@gmail.com"; // temporary
   
  // State tax
  const stateTax = data?.totalStateOwed ?? 0;

	// Fed tax
  const fedTax = (data?.totalFedOwed ?? 0);

  // Total of the tax
  const totalTax = (stateTax + fedTax).toFixed(2);

  // Determine the label for the state
  const stateLabel = stateTax >= 0 ? t("taxOverview.Owed") : t("taxOverview.Refunded");

  const fedLabel = fedTax >= 0 ? t("taxOverview.Owed") : t("taxOverview.Refunded");
 

  const options = {
    credits: {
      enabled: false
    },
    chart: {
      type: 'pie',
      backgroundColor: '#e0f7f6'
    },
    title: {
      text: '',
    },
    series: [
      {
        name: 'Data',
        data: [
          { name: 'Federal Tax ' + fedLabel, y: Number(Math.abs(fedTax).toFixed(2)) },
          { name: 'State Tax ' + stateLabel, y: Number(Math.abs(stateTax).toFixed(2)) },
        ],
        colors: ['#5abf95','#5dc0d1']
      },
    ],
  };

  return (
      <>
    <div className="flex-display-center">
      {/* <div>
        <br/><br/><br/>
        <Table
          bordered={false}
        >
          <thead>
            <tr className="bg-mint">
              <th scope="col"><span className="text-white">{t("taxOverview.tax")}</span></th>
              <th scope="col"><span className="text-white">{t("taxOverview.amountOwed")}</span></th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <th scope="row">{t("taxOverview.totalStateOwed")}</th>
              <td>$ <span className='text-mint'>{data?.totalStateOwed.toFixed(2)}</span></td>
            </tr>
            <tr>
              <th scope="row">{t("taxOverview.totalFedOwed")}</th>
              <td>$ <span className='text-mint'>{data?.totalFedOwed.toFixed(2)}</span></td>
            </tr>
            <tr>
              <th scope="row" style={{fontWeight: "bold"}}>{t("taxOverview.totalOwed")}</th>
              <td>$ <span className='text-mint' style={{fontWeight: "bold"}}>{totalTax}</span></td>
            </tr>
            </tbody>
        </Table>
        <br/><br/><br/>
      </div> */}
          <HighchartsReact highcharts={Highcharts} options={options} style={{backgroundColor: "#e0f7f6"}} />
      </div>

      </>
  )
}
