import React from "react";
import { Outlet, Navigate } from "react-router-dom";
import Landing from "../pages/Landing";
import { useSelector } from "react-redux";

const PrivateRoute = () => {
  const auth = useSelector((state: any) => state.user.isLoggedIn);

  return auth ? <Outlet /> : <Navigate to="/home" />;
};

export default PrivateRoute;
